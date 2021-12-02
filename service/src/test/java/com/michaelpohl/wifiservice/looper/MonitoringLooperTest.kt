package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.changePrivatePropertyTo
import com.michaelpohl.wifiservice.looper.MonitoringLooper.Companion.SCAN_INTERVAL_MILLIS
import com.michaelpohl.wifiservice.looper.MonitoringLooper.Companion.TURN_OFF_THRESHOLD_MILLIS
import com.michaelpohl.wifiservice.looper.MonitoringLooper.Companion.TURN_ON_THRESHOLD_MILLIS
import com.michaelpohl.wifiservice.looper.MonitoringLooper.State
import com.michaelpohl.wifiservice.repository.CellInfoRepository
import com.michaelpohl.wifiservice.repository.WifiRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
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

    private lateinit var wifiRepository: WifiRepository
    private lateinit var cellInfoRepository: CellInfoRepository

    private val states = mutableListOf<State>()

    @Before
    public override fun setUp() {
        super.setUp()
        wifiRepository = mockk()
        cellInfoRepository = mockk()
        mockkObject(CommandRunner.Companion)
        looper = MonitoringLooper(wifiRepository, cellInfoRepository) { states.add(it) }
    }

    @After
    public override fun tearDown() {
        clearAllMocks()
        states.clear()
    }

    @Test
    fun `if connected to wifi, a new state with proper time stamps  and instruction to WAIT is generated`() =
        runBlockingTest {
            // given
            every { wifiRepository.isConnectedToAnyValidSSIDs() } returns true
            every { wifiRepository.isWifiOn() } returns true

            // when
            launch {
                looper.loop()
            }
            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
            looper.stop()

            // then
            val result = states.last()
            assertNotSame(0, result.lastChecked)
            assertNotSame(0, result.lastConnected)
            assertEquals(0, result.firstCellSeen)
            assertEquals(WifiInstruction.WAIT, result.instruction)
        }

    @Test
    fun `if wifi is on, but no valid SSID around, change instruction to WAIT if threshold is not met`() = runBlockingTest {
        // given
        every { wifiRepository.isConnectedToAnyValidSSIDs() } returns true
        every { wifiRepository.isWifiOn() } returns true

        val now = Date().time
        setLooperState(
            State(instruction = WifiInstruction.TURN_OFF, lastChecked = now, lastConnected = now)
        )

        // when
        launch {
            looper.loop()
        }
        advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
        looper.stop()

        // then
        val result = states.last()
        assertNotSame(now, result.lastChecked)
        assertNotSame(now, result.lastConnected)
        assertEquals(true, result.isWifiOn)
        assertEquals(WifiInstruction.WAIT, result.instruction)
    }

    @Test
    fun `if not connected to wifi and wifi is off, a new state with proper time stamps and WAIT is generated`() =
        runBlockingTest {
            // given
            every { wifiRepository.isConnectedToAnyValidSSIDs() } returns false
            every { wifiRepository.isWifiOn() } returns false
            every { cellInfoRepository.isWithinReachOfKnownCellTowers() } returns false

            // when
            launch {
                looper.loop()
            }
            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
            looper.stop()

            // then
            val result = states.last()
            assertNotSame(0, result.lastChecked)
            assertEquals(0, result.lastConnected)
            assertEquals(0, result.firstCellSeen)
            assertEquals(WifiInstruction.WAIT, result.instruction)
        }

    @Test
    fun `if wifi is on, but no known SSID around, set instruction to TURN_OFF if threshold is met`() =
        runBlockingTest {
            // given
            every { wifiRepository.isConnectedToAnyValidSSIDs() } returns false
            every { wifiRepository.isWifiOn() } returns true
            every { cellInfoRepository.isWithinReachOfKnownCellTowers() } returns false
            val now = Date().time
            setLooperState(State(lastChecked = now, lastConnected = now - TURN_OFF_THRESHOLD_MILLIS))

            // when
            launch {
                looper.loop()
            }

            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
            looper.stop()

            // then
            val result = states.last()
            assertNotSame(now, result.lastChecked)
            assertNotSame(result.lastChecked, result.lastConnected)
            assertEquals(WifiInstruction.TURN_OFF, result.instruction)
        }

    @Test
    fun `if wifi is on post threshold, turn off even if a cell tower is around`() =
        runBlockingTest {
            // given
            every { wifiRepository.isConnectedToAnyValidSSIDs() } returns false
            every { wifiRepository.isWifiOn() } returns true
            every { cellInfoRepository.isWithinReachOfKnownCellTowers() } returns true
            val now = Date().time
            setLooperState(State(lastChecked = now, lastConnected = now - TURN_OFF_THRESHOLD_MILLIS))

            // when
            launch {
                looper.loop()
            }

            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
            looper.stop()

            // then
            val result = states.last()
            assertNotSame(now, result.lastChecked)
            assertNotSame(result.lastChecked, result.lastConnected)
            assertEquals(WifiInstruction.TURN_OFF, result.instruction)
        }

    @Test
    fun `if wifi is off and within reach of known cell, set state instruction to TURN_ON if interval is big enough`() =
        runBlockingTest {
            // given
            every { wifiRepository.isConnectedToAnyValidSSIDs() } returns false
            every { wifiRepository.isWifiOn() } returns false
            every { cellInfoRepository.isWithinReachOfKnownCellTowers() } returns true
            val now = Date().time
            setLooperState(State(lastChecked = now, firstCellSeen = now - TURN_ON_THRESHOLD_MILLIS))

            // when
            launch {
                looper.loop()
            }

            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
            looper.stop()

            // then
            val result = states.last()
            assertNotSame(now, result.lastChecked)
            assertEquals(0, result.lastConnected)
            assertEquals(now - TURN_ON_THRESHOLD_MILLIS, result.firstCellSeen)
            assertEquals(WifiInstruction.TURN_ON, result.instruction)
        }

    @Test
    fun `if wifi is off and within reach of known cell, set state instruction to WAIT if interval is not big enough`() =
        runBlockingTest {
            // given
            every { wifiRepository.isConnectedToAnyValidSSIDs() } returns false
            every { wifiRepository.isWifiOn() } returns false
            every { cellInfoRepository.isWithinReachOfKnownCellTowers() } returns true
            val now = Date().time
            setLooperState(State(lastChecked = now, firstCellSeen = now))

            // when
            launch {
                looper.loop()
            }
            advanceTimeBy(SCAN_INTERVAL_MILLIS + 1)
            looper.stop()

            // then
            val result = states.last()
            assertNotSame(now, result.lastChecked)
            assertEquals(0, result.lastConnected)
            assertEquals(now, result.firstCellSeen)
            assertEquals(WifiInstruction.WAIT, result.instruction)
        }

    private fun setLooperState(state: State) {
        looper.changePrivatePropertyTo("currentState", state)
    }
}
