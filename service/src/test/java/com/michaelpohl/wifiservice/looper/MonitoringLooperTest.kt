package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.looper.MonitoringLooper.Companion.SCAN_INTERVAL_MILLIS
import com.michaelpohl.wifiservice.repository.CellInfoRepository
import com.michaelpohl.wifiservice.repository.WifiRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MonitoringLooperTest : TestCase() {

    private lateinit var looper: MonitoringLooper

    private lateinit var wifiRepository: WifiRepository
    private lateinit var cellInfoRepository: CellInfoRepository

    private val states = mutableListOf<MonitoringLooper.State>()

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
    fun `if connected to wifi, a new state with proper time stamps is generated`() = runBlockingTest {
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

//    @Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }@Test
//    fun ``() {
//
//    }
}
