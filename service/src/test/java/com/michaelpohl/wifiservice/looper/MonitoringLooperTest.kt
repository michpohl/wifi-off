package com.michaelpohl.wifiservice.looper

//

import com.michaelpohl.wifiservice.CommandRunner
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

//
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MonitoringLooperTest : TestCase() {

    private lateinit var looper: MonitoringLooper
    private lateinit var storage: LocalStorage
    private lateinit var commandRunner: CommandRunner

    private val timing = TimingThresholds(10, 21, 21, 31)
    private val states = mutableListOf<MonitoringState>()

    @Before
    public override fun setUp() {
        super.setUp()
        commandRunner = mockk() {
            every { isWithinReachOfKnownCellTowers(any()) } returns false
        }
        storage = mockk() {
            every { savedKnownWifis } returns WifiList(listOf())
        }
        mockkObject(CommandRunner.Companion)
        looper = MonitoringLooper(commandRunner, storage, timing) { states.add(it) }
    }

    @After
    public override fun tearDown() {
        clearAllMocks()
        states.clear()
    }

    @Test
    fun `When MonitoringLooper is started, loop() is called and checks if Wifi is on`() = runTest {
        // given
        every { commandRunner.isWifiOn() } returns false
        every { storage.savedKnownWifis } returns WifiList(listOf())

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(1)
        looper.stop()

        // then
        println(states.size)

        verify(exactly = 1) { commandRunner.isWifiOn() }
    }

    @Test
    fun `With a fresh looper,Wifi is off and no known cell, state is updated with lastConnected = wifiTurnedoffAt`() =
        runTest {
            // given
            every { commandRunner.isWifiOn() } returns false
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
            every { commandRunner.isWifiOn() } returns false
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
    fun `if connected to wifi, a new state with proper time stamps and instruction to WAIT is generated`() = runTest {
        // given
        val dummyWifiList = WifiList(
            listOf(
                WifiData("ssid1", listOf("cellID1", "cellID2")),
                WifiData("ssid2", listOf("cellID3", "cellID4", "cellID5"))
            )
        )

        every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } returns true
        every { storage.savedKnownWifis } returns dummyWifiList
        every { commandRunner.isWifiOn() } returns true

        // when
        launch {
            looper.start()
        }
        advanceTimeBy(MonitoringLooper.DEFAULT_SCAN_INTERVAL_MILLIS + 1)
        looper.stop()

        // then
        val result = states.last()
        assertNotSame(0, result.lastChecked)
        assertNotSame(0, result.lastConnected)
        assertEquals(0, result.firstCellSeen)
        assertEquals(WifiInstruction.WAIT, result.instruction)
    }

//    @Test
//    fun `if wifi is on, but no valid SSID around, change instruction to WAIT if threshold is not
//    met`() = runBlockingTest
//    {
//        // given
//        val dummyWifiList = WifiList(listOf(WifiData("ssid1", "cellId1"), WifiData("ssid2", ]
//        "cellId2")))
//        every { storage.savedKnownWifis } returns dummyWifiList
//        every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } \]
//        returns true
//        every { commandRunner.isWifiOn() } returns true
//
//        val now = Date().time
//        setLooperState(
//            State(instruction = WifiInstruction.TURN_OFF, lastChecked = now, lastConnected = now)
//        )
//
//        // when
//        launch {
//            looper.loop()
//        }
//        advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
//        looper.stop()
//
//        // then
//        val result = states.last()
//        assertNotSame(now, result.lastChecked)
//        assertNotSame(now, result.lastConnected)
//        assertEquals(true, result.isWifiOn)
//        assertEquals(WifiInstruction.WAIT, result.instruction)
//    }
//
//    @Test
//    fun `if not connected to wifi and wifi is off, a new state with proper time stamps and WAIT is generated`() =
//        runBlockingTest {
//            // given
//            val dummyWifiList =
//                WifiList(listOf(WifiData("ssid1", "cellId1"), WifiData("ssid2", "cellId2")))
//            every { storage.savedKnownWifis } returns dummyWifiList
//            every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } returns false
//            every { commandRunner.isWifiOn() } returns false
//            every { commandRunner.isWithinReachOfKnownCellTowers(dummyWifiList.wifis.map { it.cellID }) } returns false
//
//            // when
//            launch {
//                looper.loop()
//            }
//            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
//            looper.stop()
//
//            // then
//            val result = states.last()
//            assertNotSame(0, result.lastChecked)
//            assertEquals(0, result.lastConnected)
//            assertEquals(0, result.firstCellSeen)
//            assertEquals(WifiInstruction.WAIT, result.instruction)
//        }
//
//    @Test
//    fun `if wifi is on, but no known SSID around, set instruction to TURN_OFF if threshold is met`() =
//        runBlockingTest {
//            // given
//            val dummyWifiList =
//                WifiList(listOf(WifiData("ssid1", "cellId1"), WifiData("ssid2", "cellId2")))
//            every { storage.savedKnownWifis } returns dummyWifiList
//            every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } returns false
//            every { commandRunner.isWifiOn() } returns true
//            every { commandRunner.isWithinReachOfKnownCellTowers(dummyWifiList.wifis.map { it.cellID }) } returns false
//            val now = Date().time
//            setLooperState(
//                State(
//                    lastChecked = now,
//                    lastConnected = now - TURN_OFF_THRESHOLD_MILLIS
//                )
//            )
//
//            // when
//            launch {
//                looper.loop()
//            }
//
//            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
//            looper.stop()
//
//            // then
//            val result = states.last()
//            assertNotSame(now, result.lastChecked)
//            assertNotSame(result.lastChecked, result.lastConnected)
//            assertEquals(WifiInstruction.TURN_OFF, result.instruction)
//        }
//
//    @Test
//    fun `if wifi is on post threshold, turn off even if a cell tower is around`() =
//        runBlockingTest {
//            // given
//            val dummyWifiList =
//                WifiList(listOf(WifiData("ssid1", "cellId1"), WifiData("ssid2", "cellId2")))
//            every { storage.savedKnownWifis } returns dummyWifiList
//            every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } returns false
//            every { commandRunner.isWifiOn() } returns true
//            every { commandRunner.isWithinReachOfKnownCellTowers(dummyWifiList.wifis.map { it.cellID }) } returns true
//            val now = Date().time
//            setLooperState(
//                State(
//                    lastChecked = now,
//                    lastConnected = now - TURN_OFF_THRESHOLD_MILLIS
//                )
//            )
//
//            // when
//            launch {
//                looper.loop()
//            }
//
//            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
//            looper.stop()
//
//            // then
//            val result = states.last()
//            assertNotSame(now, result.lastChecked)
//            assertNotSame(result.lastChecked, result.lastConnected)
//            assertEquals(WifiInstruction.TURN_OFF, result.instruction)
//        }
//
//    @Test
//    fun `if wifi is off and within reach of known cell, set state instruction to TURN_ON if i
//    nterval is big enough`() =
//    runBlockingTest
//    {
//        // given
//        val dummyWifiList =
//            WifiList(listOf(WifiData("ssid1", "cellId1"), WifiData("ssid2", "cellId2")))
//        every { storage.savedKnownWifis } returns dummyWifiList
//        every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } returns false
//        every { commandRunner.isWifiOn() } returns false
//        every { commandRunner.isWithinReachOfKnownCellTowers(dummyWifiList.wifis.map { it.cellID }) } returns true
//        val now = Date().time
//        setLooperState(State(lastChecked = now, firstCellSeen = now - TURN_ON_THRESHOLD_MILLIS))
//
//        // when
//        launch {
//            looper.loop()
//        }
//
//        advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
//        looper.stop()
//
//        // then
//        val result = states.last()
//        assertNotSame(now, result.lastChecked)
//        assertEquals(0, result.lastConnected)
//        assertEquals(now - TURN_ON_THRESHOLD_MILLIS, result.firstCellSeen)
//        assertEquals(WifiInstruction.TURN_ON, result.instruction)
//    }
//
//    @Test
//    fun `if wifi is off and within reach of known cell, set state instruction to WAIT if interval
//    is not big enough`() =
//    runBlockingTest
//    {
//        // given
//        val dummyWifiList =
//            WifiList(listOf(WifiData("ssid1", "cellId1"), WifiData("ssid2", "cellId2")))
//        every { storage.savedKnownWifis } returns dummyWifiList
//        every { commandRunner.isConnectedToAnyValidSSIDs(dummyWifiList.wifis.map { it.ssid }) } returns false
//        every { commandRunner.isWifiOn() } returns false
//        every { commandRunner.isWithinReachOfKnownCellTowers(dummyWifiList.wifis.map { it.cellID }) } returns true
//        val now = Date().time
//        setLooperState(State(lastChecked = now, firstCellSeen = now))
//
//        // when
//        launch {
//            looper.loop()
//        }
//        advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
//        looper.stop()
//
//        // then
//        val result = states.last()
//        assertNotSame(now, result.lastChecked)
//        assertEquals(0, result.lastConnected)
//        assertEquals(now, result.firstCellSeen)
//        assertEquals(WifiInstruction.WAIT, result.instruction)
//    }
//
//    private fun setLooperState(state: State) {
//        looper.changePrivatePropertyTo("currentState", state)
//    }
}
