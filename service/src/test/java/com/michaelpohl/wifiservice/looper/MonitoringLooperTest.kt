package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.changePrivatePropertyTo
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.model.WifiList
import com.michaelpohl.wifiservice.storage.LocalStorage
import io.mockk.*
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MonitoringLooperTest : TestCase() {

    private lateinit var looper: MonitoringLooper
    private lateinit var storage: LocalStorage
    private lateinit var commandRunner: CommandRunner
    private lateinit var wifiConnectionChecker: WifiConnectionChecker

    private val timings = TimingThresholds(10, 21, 21, 31)
    private val states = mutableListOf<MonitoringState>()

    @Before
    public override fun setUp() {
        super.setUp()
        wifiConnectionChecker = mockk()
        commandRunner = mockk() {
            every { isWithinReachOfKnownCellTowers(any()) } returns false
        }
        storage = mockk(relaxed = true) {
            every { savedTimings } returns timings
            every { savedKnownWifis } returns WifiList(listOf())
        }
        mockkObject(CommandRunner.Companion)
        looper = MonitoringLooper(commandRunner, wifiConnectionChecker, storage) { states.add(it) }
    }

    @After
    public override fun tearDown() {
        clearAllMocks()
        states.clear()
    }

    @Test
    fun `When MonitoringLooper is started, loop() is called and checks if Wifi is on`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns false
        every { storage.savedKnownWifis } returns WifiList(listOf())

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        Timber.d(states.size)

        verify(exactly = 1) { wifiConnectionChecker.isWifiOn() }
    }

    @Test
    fun `With a fresh looper,Wifi is off and no known cell, state is updated with lastConnected = wifiTurnedoffAt`() =
        runTest {
            // given
            every { wifiConnectionChecker.isWifiOn() } returns false
            every { storage.savedKnownWifis } returns WifiList(listOf())
            val now = Date().time

            // when
            launch {
                looper.start()
            }
            advanceTimeBy(1)
            looper.stop()

            // then
            val result = states.last()
            assertEquals("lastchecked should be same as wifiTurnedOffAt", result.lastChecked, result.wifiTurnedOffAt)
            assertEquals(
                "wifiturnedOffAt should be roughly equal the execution time of the test",
                result.wifiTurnedOffAt!!.toDouble(),
                now.toDouble(),
                20.toDouble()
            )
            assertEquals("WifiIstruction.WAIT is expected", result.instruction, WifiInstruction.WAIT)
        }

    @Test
    fun `A fresh MonitoringLooper with no wifi and no close cell should produce 2 state updates when looping once`() =
        runTest {
            // given
            every { wifiConnectionChecker.isWifiOn() } returns false
            every { storage.savedKnownWifis } returns WifiList(listOf())

            // when
            launch {
                looper.start()
            }
            advanceTimeBy(1)
            looper.stop()

            // then
            assertEquals(
                """A fresh MonitoringLooper with no wifi and no close cell 
                should produce 2 state updates when looping once""".trimMargin(), states.size, 2
            )
        }

    @Test
    fun `If connected to a wifi, lastConnected gets set to now, instruction is WAIT`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns true
        every { wifiConnectionChecker.isConnectedToAnyWifi() } returns true
        every { commandRunner.getCurrentConnectedWifi() } returns WifiData("ssid", listOf())

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(25)
        looper.stop()

        // then
        val result = states.last()
        assertEquals("Instruction should be WAIT", WifiInstruction.WAIT, result.instruction)
    }

    @Test
    fun `If connected to a known wifi, cellIDS get updated if different`() = runTest {
        // given
        val savedDummyWifi = WifiData("ssid", listOf("123", "456"))
        val updatedDummyWifi = WifiData("ssid", listOf("456", "789"))
        val expectedWifiToBeSaved = WifiData("ssid", listOf("123", "456", "789")) // we expect to save all found cellIDs
        every { wifiConnectionChecker.isWifiOn() } returns true
        every { wifiConnectionChecker.isConnectedToAnyWifi() } returns true
        every { commandRunner.getCurrentConnectedWifi() } returns updatedDummyWifi
        every { storage.savedKnownWifis } returns WifiList(listOf(savedDummyWifi))
        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        val result = states.last()
        verify(exactly = 1) { storage.saveWifi(expectedWifiToBeSaved) }
    }

    @Test
    fun `If not connected to wifi, turn wifi off once past turnOffThreshold`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns true
        every { wifiConnectionChecker.isConnectedToAnyWifi() } returns false
        val now = Date().time - 500
        setLooperState(MonitoringState(lastConnected = now))
        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        val result = states.last()
        assertEquals(WifiInstruction.TURN_OFF, result.instruction)
    }

    @Test
    fun `If not connected to wifi, don't turn off before turnOffThreshold is reached`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns true
        every { wifiConnectionChecker.isConnectedToAnyWifi() } returns false
        val now = Date().time + 1000 // we add a generous 1000ms to offset the time the test might take to run
        setLooperState(MonitoringState(lastConnected = now))
        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        val result = states.last()
        assertEquals(WifiInstruction.WAIT, result.instruction)
    }

    @Test
    fun `If not connected to wifi and lastConnected is not set, it gets set with the next status update`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns true
        every { wifiConnectionChecker.isConnectedToAnyWifi() } returns false

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        assertNull("First state should have lastConnected not set", states.first().lastConnected)
        assertNotNull("Last state should have lastConnected set", states.last().lastConnected)
    }

    @Test
    fun `If wifi is off do nothing before wifiTurnedOffMinThreshold is reached`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns false

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        assertEquals(
            "Instruction should be WAIT when threshold is not reached", WifiInstruction.WAIT, states.last().instruction
        )
    }

    @Test
    fun `If wifi is off and wifiTurnedOffAt is not set, set it on the next status update`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns false

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        assertNull("First state should have wifiTurnedOffAt not set", states.first().wifiTurnedOffAt)
        assertNotNull("Last state should have wifiTurnedOffAt set", states.last().wifiTurnedOffAt)
    }

    @Test
    fun `If wifiTurnedOffMinThreshold is reached, just wait if not within reach of known cell`() = runTest {
        // given
        every { wifiConnectionChecker.isWifiOn() } returns false
        every { commandRunner.isWithinReachOfKnownCellTowers(any()) } returns false

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(30)
        looper.stop()

        // then
        assertEquals(
            "Instruction should be WAIT if no known cell is around", WifiInstruction.WAIT, states.last().instruction
        )
    }

    @Test
    fun `If wifiTurnedOffThreshold is reached, turn on wifi if a known cell is around and turnOnThreshold is passed`() =
        runTest {
            // given
            every { wifiConnectionChecker.isWifiOn() } returns false
            every { commandRunner.isWithinReachOfKnownCellTowers(any()) } returns true
            val now = Date().time
            setLooperState(MonitoringState(wifiTurnedOffAt = now - 500)) // move it back to past more than threshold

            // when
            launch {
                looper.start()
            }
            advanceTimeBy(1)
            looper.stop()

            // then
            assertEquals("", WifiInstruction.TURN_ON, states.last().instruction)
        }

    @Test
    fun `If wifi is off & wifiTurnedOffThreshold is reached but turnOnThreshold is not, just wait`() = runTest {

        // given
        every { wifiConnectionChecker.isWifiOn() } returns false
        every { commandRunner.isWithinReachOfKnownCellTowers(any()) } returns true
        val now = Date().time
        looper = MonitoringLooper(commandRunner, wifiConnectionChecker, storage) { states.add(it) }

        setLooperState(MonitoringState(wifiTurnedOffAt = now - 5)) // move it back to past less than threshold

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(5)
        looper.stop()

        // then
        assertEquals("Incorrect Looper instruction:", WifiInstruction.WAIT, states.last().instruction)
    }

    private fun setLooperState(state: MonitoringState) {
        looper.changePrivatePropertyTo("currentState", state)
    }
}
