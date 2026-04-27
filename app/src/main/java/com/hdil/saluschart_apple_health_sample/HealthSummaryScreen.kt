package com.hdil.saluschart_apple_health_sample

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.hdil.saluschart.core.chart.BaseChartMark
import com.hdil.saluschart.core.chart.ChartMark
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.core.chart.ProgressChartMark
import com.hdil.saluschart.core.chart.RangeChartMark
import com.hdil.saluschart.core.chart.ReferenceLineSpec
import com.hdil.saluschart.core.chart.chartDraw.LineStyle
import com.hdil.saluschart.core.chart.chartDraw.ReferenceLineType
import com.hdil.saluschart.core.chart.chartDraw.YAxisPosition
import com.hdil.saluschart.core.chart.toRangeChartMarksByXGroup
import com.hdil.saluschart.core.transform.transform
import com.hdil.saluschart.core.util.AggregationType
import com.hdil.saluschart.core.util.TimeUnitGroup
import com.hdil.saluschart.data.model.model.HeartRate
import com.hdil.saluschart.data.model.model.HeartRateSample
import com.hdil.saluschart.ui.compose.charts.LineChart
import com.hdil.saluschart.ui.compose.charts.MiniActivityRings
import com.hdil.saluschart.ui.compose.charts.MinimalBarChart
import com.hdil.saluschart.ui.compose.charts.MinimalLineChart
import com.hdil.saluschart.ui.compose.charts.MinimalRangeBarChart
import com.hdil.saluschart.ui.compose.charts.RangeBarChart
import com.hdil.saluschart.data.model.model.SleepSession
import com.hdil.saluschart.data.model.model.SleepStage
import com.hdil.saluschart.data.model.model.SleepStageType
import com.hdil.saluschart.ui.compose.charts.MinimalSleepStageChart
import com.hdil.saluschart.ui.compose.charts.SleepStageChart
import com.hdil.saluschart.ui.theme.LocalSalusChartColors
import com.hdil.saluschart.ui.theme.SalusChartColorScheme
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


private val AHBackground = Color(0xFFF2F2F7)
private val AHCard = Color.White
private val AHBlue = Color(0xFF007AFF)
private val AHGray = Color(0xFF8E8E93)
private val AHSeparator = Color(0xFFE5E5EA)
private val AHGreen = Color(0xFF34C759)
private val AHChartGray = Color(0xFFD1D1D6)

private val AHSteps = Color(0xFFFF9500)
private val AHHeartRate = Color(0xFFFC3D56)
private val AHSleep = Color(0xFF5E5CE6)
private val AHWeight = Color(0xFFFF9F0A)
private val AHHRV = Color(0xFFFC3D56)

private val RingMove = Color(0xFFFA3E5B)
private val RingExercise = Color(0xFF91F04F)
private val RingStand = Color(0xFF00D4FF)

private val AHSleepDeep = Color(0xFF1C3657)
private val AHSleepCore = Color(0xFF3A7BD5)
private val AHSleepREM = Color(0xFF7B61FF)
private val AHSleepAwake = Color(0xFFBDBDBF)
private val AHSleepAwakePink = Color(0xFFFF6B81)
private val AHSleepREMCyan   = Color(0xFF5AC8FA)

// ── Sample data ───────────────────────────────────────────────────────────────

private val stepsData = listOf(
    ChartMark(0.0, 4200.0), ChartMark(1.0, 7800.0), ChartMark(2.0, 6100.0),
    ChartMark(3.0, 9340.0), ChartMark(4.0, 5500.0), ChartMark(5.0, 8200.0),
    ChartMark(6.0, 3100.0),
)
private fun getHeartRateData(): List<HeartRate> {
    // 3 weeks: Jul 11–31, 2025 (Fri–Thu)
    val dayBpms = listOf(
        // ── Week 1: Jul 11–17 ───────────────────────────────────────────────
        // Fri Jul 11 - Normal day
        listOf(57, 60, 63, 66, 69, 71, 73, 75, 78, 80, 83, 86, 89, 93, 98, 104, 112, 125, 138),
        // Sat Jul 12 - Workout
        listOf(53, 56, 60, 64, 68, 72, 76, 80, 85, 90, 96, 102, 109, 116, 124, 133, 144, 155, 163, 170),
        // Sun Jul 13 - Rest
        listOf(51, 54, 57, 59, 61, 63, 65, 67, 69, 71, 73, 75, 77, 80, 83, 88, 94, 101),
        // Mon Jul 14 - Regular
        listOf(56, 59, 62, 65, 68, 70, 73, 76, 79, 82, 85, 89, 93, 98, 105, 114, 126, 140),
        // Tue Jul 15 - Easy
        listOf(55, 58, 61, 63, 65, 67, 69, 72, 74, 77, 80, 83, 87, 92, 98, 106, 116),
        // Wed Jul 16 - Active
        listOf(54, 57, 61, 65, 68, 72, 76, 80, 85, 90, 95, 101, 108, 115, 123, 132, 143, 154, 162),
        // Thu Jul 17 - Regular
        listOf(56, 59, 62, 65, 67, 70, 72, 75, 78, 81, 84, 88, 93, 99, 106, 115, 126),
        // ── Week 2: Jul 18–24 ───────────────────────────────────────────────
        // Fri Jul 18 - Normal
        listOf(55, 58, 61, 64, 67, 70, 72, 74, 77, 79, 82, 85, 88, 92, 97, 103, 113, 128, 142),
        // Sat Jul 19 - Long run
        listOf(52, 55, 59, 63, 67, 71, 75, 80, 85, 91, 97, 104, 111, 119, 128, 138, 149, 158, 166, 172),
        // Sun Jul 20 - Rest
        listOf(51, 53, 56, 58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 79, 82, 87, 93, 100),
        // Mon Jul 21 - Busy
        listOf(57, 60, 63, 66, 69, 72, 74, 77, 80, 83, 86, 90, 95, 100, 107, 116, 128, 142, 152),
        // Tue Jul 22 - Easy
        listOf(54, 57, 60, 62, 64, 67, 69, 71, 74, 76, 79, 82, 86, 91, 97, 104, 114),
        // Wed Jul 23 - Gym
        listOf(55, 58, 62, 65, 69, 72, 76, 80, 84, 89, 94, 100, 107, 114, 122, 131, 141, 152, 161),
        // Thu Jul 24 - Regular
        listOf(55, 58, 61, 64, 67, 69, 72, 74, 77, 80, 83, 87, 91, 96, 103, 111, 122),
        // ── Week 3: Jul 25–31 ───────────────────────────────────────────────
        // Fri Jul 25 - Normal workday (mild activity)
        listOf(56, 59, 62, 65, 68, 70, 72, 74, 76, 78, 80, 82, 85, 88, 92, 96, 102, 115, 130, 145),
        // Sat Jul 26 - Weekend workout (very active)
        listOf(54, 57, 61, 65, 69, 72, 76, 80, 84, 88, 93, 98, 104, 110, 118, 126, 135, 148, 160, 172),
        // Sun Jul 27 - Rest / Recovery
        listOf(52, 55, 57, 59, 62, 64, 66, 67, 68, 70, 71, 72, 74, 76, 78, 81, 85, 90, 96, 104),
        // Mon Jul 28 - Busy workday (moderate)
        listOf(57, 60, 63, 66, 69, 71, 73, 76, 79, 82, 85, 88, 92, 97, 103, 110, 120, 132, 145, 155),
        // Tue Jul 29 - Easy / light day
        listOf(54, 57, 60, 63, 65, 67, 69, 71, 73, 76, 78, 81, 84, 88, 93, 99, 106, 118),
        // Wed Jul 30 - Active afternoon (gym)
        listOf(55, 58, 62, 65, 68, 71, 75, 79, 83, 87, 91, 96, 101, 107, 114, 122, 132, 142, 155, 165),
        // Thu Jul 31 - Regular day
        listOf(55, 58, 61, 64, 67, 70, 72, 74, 77, 80, 83, 86, 90, 95, 101, 108, 118, 128),
    )
    val dayStarts = (0 until 21).map { i ->
        Instant.parse("2025-07-11T00:00:00Z").plusSeconds(i * 86400L)
    }
    return dayStarts.zip(dayBpms).map { (dayStart, bpms) ->
        HeartRate(
            startTime = dayStart,
            endTime = dayStart.plusSeconds(86400L),
            samples = bpms.mapIndexed { i, bpm ->
                HeartRateSample(
                    time = dayStart.plusSeconds(i * 2400L), // 40분 간격
                    beatsPerMinute = bpm
                )
            }
        )
    }
}
private val weightData = listOf(
    ChartMark(0.0, 73.2), ChartMark(1.0, 73.0), ChartMark(2.0, 72.8),
    ChartMark(3.0, 72.9), ChartMark(4.0, 72.5), ChartMark(5.0, 72.3),
    ChartMark(6.0, 72.1),
)
private val hrTrendData = listOf(
    ChartMark(0.0, 74.0), ChartMark(1.0, 72.0), ChartMark(2.0, 73.0),
    ChartMark(3.0, 71.0), ChartMark(4.0, 70.0), ChartMark(5.0, 72.0),
    ChartMark(6.0, 69.0), ChartMark(7.0, 71.0), ChartMark(8.0, 68.0),
    ChartMark(9.0, 70.0), ChartMark(10.0, 67.0), ChartMark(11.0, 69.0),
    ChartMark(12.0, 68.0), ChartMark(13.0, 70.0), ChartMark(14.0, 67.0),
    ChartMark(15.0, 68.0), ChartMark(16.0, 66.0), ChartMark(17.0, 68.0),
    ChartMark(18.0, 67.0), ChartMark(19.0, 69.0), ChartMark(20.0, 66.0),
    ChartMark(21.0, 68.0), ChartMark(22.0, 65.0), ChartMark(23.0, 67.0),
    ChartMark(24.0, 65.0), ChartMark(25.0, 64.0),
)
private val stepsTrendData = listOf(
    ChartMark(0.0, 6800.0), ChartMark(1.0, 7200.0), ChartMark(2.0, 6500.0),
    ChartMark(3.0, 7800.0), ChartMark(4.0, 7100.0), ChartMark(5.0, 8300.0),
    ChartMark(6.0, 7600.0), ChartMark(7.0, 8900.0), ChartMark(8.0, 7400.0),
    ChartMark(9.0, 8200.0), ChartMark(10.0, 9100.0), ChartMark(11.0, 7900.0),
    ChartMark(12.0, 8600.0), ChartMark(13.0, 7300.0), ChartMark(14.0, 8800.0),
    ChartMark(15.0, 8100.0), ChartMark(16.0, 9200.0), ChartMark(17.0, 7700.0),
    ChartMark(18.0, 8400.0), ChartMark(19.0, 9000.0), ChartMark(20.0, 8300.0),
    ChartMark(21.0, 8700.0), ChartMark(22.0, 9100.0), ChartMark(23.0, 8500.0),
    ChartMark(24.0, 9000.0), ChartMark(25.0, 9340.0),
)
private val exerciseMinutesData = listOf(
    ChartMark(0.0, 28.0), ChartMark(1.0, 35.0), ChartMark(2.0, 22.0),
    ChartMark(3.0, 41.0), ChartMark(4.0, 18.0), ChartMark(5.0, 38.0),
    ChartMark(6.0, 30.0),
)
private val environmentalSoundLevelsData = listOf(
    ChartMark(0.0, 67.0), ChartMark(0.0, 74.0),
    ChartMark(1.0, 62.0), ChartMark(1.0, 81.0),
    ChartMark(2.0, 65.0), ChartMark(2.0, 80.0),
    ChartMark(3.0, 64.0), ChartMark(3.0, 79.0),
    ChartMark(4.0, 66.0), ChartMark(4.0, 78.0),
    ChartMark(5.0, 63.0), ChartMark(5.0, 82.0),
    ChartMark(6.0, 58.0), ChartMark(6.0, 73.0),
)
private val sleepTrendData = listOf(
    ChartMark(0.0, 6.5), ChartMark(1.0, 7.0), ChartMark(2.0, 6.8),
    ChartMark(3.0, 7.2), ChartMark(4.0, 7.5), ChartMark(5.0, 7.7),
    ChartMark(6.0, 7.7),
)
private val sleepSession: SleepSession = run {
    val base = Instant.ofEpochMilli(1712235600000L)
    fun m(mins: Long): Instant = base.plusSeconds(mins * 60L)
    SleepSession(
        startTime = m(0),
        endTime = m(404),
        stages = listOf(
            SleepStage(m(0), m(20), SleepStageType.AWAKE),
            SleepStage(m(20), m(140), SleepStageType.LIGHT),
            SleepStage(m(140), m(210), SleepStageType.DEEP),
            SleepStage(m(210), m(240), SleepStageType.LIGHT),
            SleepStage(m(240), m(300), SleepStageType.REM),
            SleepStage(m(300), m(330), SleepStageType.LIGHT),
            SleepStage(m(330), m(360), SleepStageType.DEEP),
            SleepStage(m(360), m(380), SleepStageType.REM),
            SleepStage(m(380), m(404), SleepStageType.AWAKE),
        )
    )
}

private val sleepColorScheme = SalusChartColorScheme(
    primary = AHSleepCore,
    secondary = AHSleepAwake,
    palette = listOf(AHSleepDeep, AHSleepCore, AHSleepREM, AHSleepAwake),
)
private val sleepDetailColorScheme = SalusChartColorScheme(
    primary   = AHSleepCore,
    secondary = AHSleepAwakePink,
    palette   = listOf(AHSleepDeep, AHSleepCore, AHSleepREMCyan, AHSleepAwakePink),
)
// Aug 10, 2025 9 PM → Aug 11, 2025 6:33 AM (9h33m total, 7h33m asleep)
private val sleepDetailSession: SleepSession = run {
    val base = Instant.ofEpochMilli(1754859600000L) // 2025-08-10 21:00:00 UTC
    fun m(mins: Long): Instant = base.plusSeconds(mins * 60L)
    SleepSession(
        startTime = m(0),
        endTime   = m(573),
        stages = listOf(
            SleepStage(m(0),   m(45),  SleepStageType.AWAKE),
            SleepStage(m(45),  m(135), SleepStageType.LIGHT),
            SleepStage(m(135), m(165), SleepStageType.DEEP),
            SleepStage(m(165), m(175), SleepStageType.AWAKE),
            SleepStage(m(175), m(190), SleepStageType.LIGHT),
            SleepStage(m(190), m(205), SleepStageType.AWAKE),
            SleepStage(m(205), m(235), SleepStageType.LIGHT),
            SleepStage(m(235), m(265), SleepStageType.DEEP),
            SleepStage(m(265), m(295), SleepStageType.LIGHT),
            SleepStage(m(295), m(325), SleepStageType.REM),
            SleepStage(m(325), m(340), SleepStageType.AWAKE),
            SleepStage(m(340), m(385), SleepStageType.LIGHT),
            SleepStage(m(385), m(415), SleepStageType.DEEP),
            SleepStage(m(415), m(445), SleepStageType.LIGHT),
            SleepStage(m(445), m(475), SleepStageType.REM),
            SleepStage(m(475), m(505), SleepStageType.LIGHT),
            SleepStage(m(505), m(520), SleepStageType.AWAKE),
            SleepStage(m(520), m(555), SleepStageType.LIGHT),
            SleepStage(m(555), m(573), SleepStageType.AWAKE),
        )
    )
}
private val bloodOxygenBarMins = listOf(
    96.0, 97.5, 98.5, 95.0, 97.0, 96.5, 97.0, 96.5, 97.5, 98.0,
    96.5, 96.0, 97.5, 98.5, 96.0, 95.5, 97.5, 97.0, 96.0, 96.5,
    97.5, 95.5, 98.5, 96.0, 97.0, 97.5
)
private val bloodOxygenDots: List<List<Double>> = listOf(
    listOf(98.5, 97.0),        // 0  above ref
    listOf(94.5, 98.0),        // 1  one below, one above
    listOf(99.0),              // 2  above ref
    listOf(93.0, 97.5),        // 3  one below, one above
    listOf(98.0, 96.5),        // 4  above ref
    listOf(94.0, 93.5, 97.0),  // 5  two below, one above
    listOf(99.0, 97.5),        // 6  above ref
    listOf(98.5),              // 7  above ref
    listOf(94.0, 97.0, 98.5),  // 8  one below, two above
    listOf(99.0, 96.5),        // 9  above ref
    listOf(93.5, 98.0),        // 10 one below, one above
    listOf(97.5, 99.0),        // 11 above ref
    listOf(98.0, 96.5),        // 12 above ref
    listOf(94.5, 97.0),        // 13 one below, one above
    listOf(98.5, 99.0),        // 14 above ref
    listOf(93.0, 94.0, 98.0),  // 15 two below, one above
    listOf(97.5),              // 16 above ref
    listOf(94.5, 99.0, 97.0),  // 17 one below, two above
    listOf(98.5, 96.5),        // 18 above ref
    listOf(99.0, 97.5),        // 19 above ref
    listOf(93.5, 98.0),        // 20 one below, one above
    listOf(96.5, 98.5),        // 21 above ref
    listOf(94.0, 97.0),        // 22 one below, one above
    listOf(99.0, 98.0),        // 23 above ref
    listOf(97.5, 96.0),        // 24 above ref
    listOf(94.0, 98.5),        // 25 one below, one above
)
private val activityRings = listOf(
    ProgressChartMark(x = 0.0, current = 420.0, max = 600.0, label = "Move", unit = "kcal"),
    ProgressChartMark(x = 1.0, current = 41.0, max = 60.0, label = "Exercise", unit = "min"),
    ProgressChartMark(x = 2.0, current = 13.0, max = 16.0, label = "Stand", unit = "hr"),
)
private val ringColors = listOf(RingMove, RingExercise, RingStand)

// Monthly activity data (31 days) for Activity detail M-period view
// Move (goal=600 kcal): 19 of 31 days above goal
private val activityMoveMonthly = listOf(
    ChartMark(0.0, 450.0), ChartMark(1.0, 680.0), ChartMark(2.0, 320.0), ChartMark(3.0, 750.0),
    ChartMark(4.0, 200.0), ChartMark(5.0, 640.0), ChartMark(6.0, 610.0), ChartMark(7.0, 710.0),
    ChartMark(8.0, 430.0), ChartMark(9.0, 650.0), ChartMark(10.0, 280.0), ChartMark(11.0, 700.0),
    ChartMark(12.0, 500.0), ChartMark(13.0, 770.0), ChartMark(14.0, 350.0), ChartMark(15.0, 640.0),
    ChartMark(16.0, 180.0), ChartMark(17.0, 730.0), ChartMark(18.0, 620.0), ChartMark(19.0, 680.0),
    ChartMark(20.0, 610.0), ChartMark(21.0, 790.0), ChartMark(22.0, 550.0), ChartMark(23.0, 660.0),
    ChartMark(24.0, 380.0), ChartMark(25.0, 720.0), ChartMark(26.0, 630.0), ChartMark(27.0, 640.0),
    ChartMark(28.0, 310.0), ChartMark(29.0, 760.0), ChartMark(30.0, 590.0)
)
// Exercise (goal=30 min): 3 of 31 days above goal
private val activityExerciseMonthly = listOf(
    ChartMark(0.0, 8.0), ChartMark(1.0, 12.0), ChartMark(2.0, 5.0), ChartMark(3.0, 15.0),
    ChartMark(4.0, 10.0), ChartMark(5.0, 45.0), ChartMark(6.0, 8.0), ChartMark(7.0, 18.0),
    ChartMark(8.0, 5.0), ChartMark(9.0, 20.0), ChartMark(10.0, 12.0), ChartMark(11.0, 7.0),
    ChartMark(12.0, 15.0), ChartMark(13.0, 38.0), ChartMark(14.0, 10.0), ChartMark(15.0, 5.0),
    ChartMark(16.0, 20.0), ChartMark(17.0, 8.0), ChartMark(18.0, 15.0), ChartMark(19.0, 12.0),
    ChartMark(20.0, 5.0), ChartMark(21.0, 42.0), ChartMark(22.0, 10.0), ChartMark(23.0, 18.0),
    ChartMark(24.0, 8.0), ChartMark(25.0, 12.0), ChartMark(26.0, 5.0), ChartMark(27.0, 20.0),
    ChartMark(28.0, 8.0), ChartMark(29.0, 15.0), ChartMark(30.0, 10.0)
)
// Stand (goal=12 hr): 22 of 31 days above goal
private val activityStandMonthly = listOf(
    ChartMark(0.0, 14.0), ChartMark(1.0, 8.0), ChartMark(2.0, 13.0), ChartMark(3.0, 15.0),
    ChartMark(4.0, 10.0), ChartMark(5.0, 14.0), ChartMark(6.0, 7.0), ChartMark(7.0, 15.0),
    ChartMark(8.0, 13.0), ChartMark(9.0, 16.0), ChartMark(10.0, 8.0), ChartMark(11.0, 14.0),
    ChartMark(12.0, 13.0), ChartMark(13.0, 6.0), ChartMark(14.0, 15.0), ChartMark(15.0, 14.0),
    ChartMark(16.0, 9.0), ChartMark(17.0, 13.0), ChartMark(18.0, 16.0), ChartMark(19.0, 14.0),
    ChartMark(20.0, 5.0), ChartMark(21.0, 15.0), ChartMark(22.0, 13.0), ChartMark(23.0, 14.0),
    ChartMark(24.0, 7.0), ChartMark(25.0, 16.0), ChartMark(26.0, 13.0), ChartMark(27.0, 14.0),
    ChartMark(28.0, 9.0), ChartMark(29.0, 15.0), ChartMark(30.0, 14.0)
)

// 오늘 시간대별 걸음수 (오전 활동 → 오후 저조 패턴, 합계 ~394)
private val stepsPinnedData = listOf(
    ChartMark(0.0, 0.0),
    ChartMark(1.0, 82.0),
    ChartMark(2.0, 148.0),
    ChartMark(3.0, 96.0),
    ChartMark(4.0, 48.0),
    ChartMark(5.0, 20.0),
)

// 마지막 바만 주황색으로 표시하기 위한 오버레이용 데이터 (회색 레이어)
// 마지막 인덱스 y≈0 → 높이 거의 0 → 아래 주황 바가 드러남
private val stepsPinnedDataGrayOverlay = stepsPinnedData.mapIndexed { i, mark ->
    if (i == stepsPinnedData.lastIndex) ChartMark(mark.x, 0.001) else mark
}

// 피닝 카드용 소수 포인트 데이터 (Apple Health 소형 차트 스타일)
private val hrvPinnedData = listOf(
    ChartMark(0.0, 38.0), ChartMark(1.0, 46.0),
    ChartMark(2.0, 41.0), ChartMark(3.0, 45.0),
)
private val hrPinnedData = listOf(
    ChartMark(0.0, 83.0), ChartMark(1.0, 80.0),
    ChartMark(2.0, 75.0), ChartMark(3.0, 83.0),
)

private val hrvData = listOf(
    ChartMark(0.0, 42.0, label = "Mon"), ChartMark(1.0, 48.0, label = "Tue"),
    ChartMark(2.0, 45.0, label = "Wed"), ChartMark(3.0, 51.0, label = "Thu"),
    ChartMark(4.0, 43.0, label = "Fri"), ChartMark(5.0, 47.0, label = "Sat"),
    ChartMark(6.0, 45.0, label = "Sun"),
)

// ── Root screen ───────────────────────────────────────────────────────────────

private enum class PinnedDetail { ACTIVITY, HRV, HEART_RATE, SLEEP, STEPS, WEIGHT }

@Composable
fun HealthSummaryScreen() {
    var detail by remember { mutableStateOf<PinnedDetail?>(null) }

    if (detail != null) {
        BackHandler { detail = null }
        PinnedDetailScreen(detail!!) { detail = null }
        return
    }

    Scaffold(
        containerColor = AHBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            GradientHeader()

            SectionHeader("Pinned", "Edit")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivityPinnedCard(onClick = { detail = PinnedDetail.ACTIVITY })
                HRVPinnedCard(onClick = { detail = PinnedDetail.HRV })
                HeartRatePinnedCard(onClick = { detail = PinnedDetail.HEART_RATE })
                SleepPinnedCard(onClick = { detail = PinnedDetail.SLEEP })
                StepsPinnedCard(onClick = { detail = PinnedDetail.STEPS })
                WeightPinnedCard(onClick = { detail = PinnedDetail.WEIGHT })
            }

            Spacer(Modifier.height(16.dp))
            ShowAllNavRow(icon = "♥", text = "Show All Health Data")

            SectionHeader("Trends", null)
            ShowAllNavRow(icon = "◁", text = "Show All Health Trends")
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeartRateTrendCard()
                StepsTrendCard()
                BloodOxygenTrendCard()
            }

            SectionHeader("Highlights", null)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StepsHighlightCard()
                ExerciseHighlightCard()
                EnvironmentalSoundLevelsHighlightCard()
            }
            Spacer(Modifier.height(16.dp))
            ShowAllNavRow(icon = "≡", text = "Show All Highlights")

            SectionHeader("Get More From Health", null)
            MedicalIDPromoCard()

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Gradient header ───────────────────────────────────────────────────────────

@Composable
private fun GradientHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AHBackground)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Summary",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1D1D6)),
                contentAlignment = Alignment.Center
            ) {
                Text("S", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────

// ── Navigation row ("Show All...") ────────────────────────────────────────────

@Composable
private fun ShowAllNavRow(icon: String, text: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AHCard)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F2F7)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text("›", fontSize = 20.sp, color = AHGray)
        }
    }
}

// ── Medical ID promo card ─────────────────────────────────────────────────────

@Composable
private fun MedicalIDPromoCard() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text("✚", fontSize = 26.sp, color = Color(0xFFFC3D56))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Update Your Medical ID",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "In an emergency, it's important that first responders have up-to-date information.",
                    fontSize = 13.sp,
                    color = AHGray,
                    lineHeight = 17.sp
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AHBlue)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Get Started", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action != null) Text(action, color = AHBlue, fontSize = 15.sp)
    }
}

// ── Card shell ────────────────────────────────────────────────────────────────

@Composable
private fun AHCardContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard),
        content = content
    )
}

@Composable
private fun CardHairline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(AHSeparator)
    )
}

// ── Pinned cards ──────────────────────────────────────────────────────────────

@Composable
private fun PinnedCard(
    icon: String,
    title: String,
    titleColor: Color = AHGray,
    value: String,
    unit: String,
    label: String = "",
    period: String = "",
    onClick: () -> Unit = {},
    chart: @Composable BoxScope.() -> Unit
) {
    AHCardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // 상단 행: 아이콘+제목 (좌) | period + › (우)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 14.sp)
                Spacer(Modifier.width(5.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
                Spacer(Modifier.weight(1f))
                if (period.isNotEmpty()) {
                    Text(
                        text = "$period ›",
                        fontSize = 13.sp,
                        color = AHGray
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            // 하단 행: 라벨+값 (좌) | 차트 (우)
            Row(
                modifier = Modifier.fillMaxWidth().height(68.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (label.isNotEmpty()) {
                        Text(label, fontSize = 12.sp, color = AHGray)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        if (unit.isNotEmpty()) {
                            Spacer(Modifier.width(3.dp))
                            Text(
                                unit, fontSize = 13.sp, color = AHGray,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.width(110.dp).fillMaxHeight(),
                    content = chart
                )
            }
        }
    }
}

@Composable
private fun StepsPinnedCard(onClick: () -> Unit = {}) {
    PinnedCard("🔥", "Steps", titleColor = AHSteps, "394", "steps", period = "1:11 PM", onClick = onClick) {
        // 두 개의 MinimalBarChart를 겹쳐서 마지막 바만 주황색으로 표시
        // Layer 1 (아래): 전체 데이터를 주황색으로 그림
        // Layer 2 (위): 마지막 바 y≈0인 회색 바로 덮음 → 마지막만 주황이 드러남
        MinimalBarChart(
            modifier = Modifier.fillMaxSize(),
            data = stepsPinnedData,
            color = AHSteps,
            barWidthRatio = 0.55f,
            roundTopOnly = true,
            barCornerRadiusFraction = 0.15f,
            padding = 0f,
        )
        MinimalBarChart(
            modifier = Modifier.fillMaxSize(),
            data = stepsPinnedDataGrayOverlay,
            color = Color(0xFFD1D1D6),
            barWidthRatio = 0.55f,
            roundTopOnly = true,
            barCornerRadiusFraction = 0.15f,
            padding = 0f,
        )
    }
}

@Composable
private fun HeartRatePinnedCard(onClick: () -> Unit = {}) {
    PinnedCard("♥", "Resting Heart Rate", titleColor = AHHeartRate, "83", "BPM", period = "Yesterday", onClick = onClick) {
        MinimalLineChart(
            modifier = Modifier.fillMaxSize(),
            data = hrPinnedData,
            color = AHHeartRate,
            strokeWidth = 2f,
            padding = 4f,
            showPoints = true,
        )
    }
}

@Composable
private fun HRVPinnedCard(onClick: () -> Unit = {}) {
    PinnedCard("♥", "Heart Rate Variability", titleColor = AHHRV, "45", "ms", label = "Average", period = "Yesterday", onClick = onClick) {
        MinimalLineChart(
            modifier = Modifier.fillMaxSize(),
            data = hrvPinnedData,
            color = AHHRV,
            strokeWidth = 2f,
            padding = 4f,
            showPoints = true,
        )
    }
}

@Composable
private fun SleepPinnedCard(onClick: () -> Unit = {}) {
    PinnedCard("🛏", "Sleep", titleColor = AHSleep, "6", "hr 24 min", label = "Time Asleep", period = "Apr 4", onClick = onClick) {
        CompositionLocalProvider(LocalSalusChartColors provides sleepColorScheme) {
            MinimalSleepStageChart(
                modifier = Modifier.fillMaxSize(),
                sleepSession = sleepSession,
                barHeightRatio = 0.5f,
                minBarWidthRatio = 0.3f,
                cornerRadiusRatio = 0.24f,
                connectorWidthRatio = 0.12f,
            )
        }
    }
}

@Composable
private fun ActivityPinnedCard(onClick: () -> Unit = {}) {
    AHCardContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // 상단 행: PinnedCard와 동일한 구조
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔥", fontSize = 14.sp)
                Spacer(Modifier.width(5.dp))
                Text("Activity", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RingMove)
                Spacer(Modifier.weight(1f))
                Text("3:20 AM ›", fontSize = 13.sp, color = AHGray)
            }
            Spacer(Modifier.height(10.dp))
            // 하단 행: PinnedCard와 동일한 height(68dp)
            Row(
                modifier = Modifier.fillMaxWidth().height(68.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActivityStatColumn("Move", "420", "kcal", RingMove, modifier = Modifier.weight(1f))
                    VerticalHairline()
                    ActivityStatColumn("Exercise", "41", "min", RingExercise, modifier = Modifier.weight(1f))
                    VerticalHairline()
                    ActivityStatColumn("Stand", "13", "hr", RingStand, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    MiniActivityRings(
                        modifier = Modifier.size(60.dp),
                        rings = activityRings,
                        colors = ringColors,
                        strokeWidth = 14f,
                        trackAlpha = 0.25f
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityStatColumn(
    label: String,
    value: String,
    unit: String,
    labelColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 8.dp), horizontalAlignment = Alignment.Start) {
        Text(label, fontSize = 12.sp, color = labelColor, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(2.dp))
            Text(unit, fontSize = 12.sp, color = AHGray, modifier = Modifier.offset(y = 2.dp))
        }
    }
}

@Composable
private fun VerticalHairline() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(44.dp)
            .background(AHSeparator)
    )
}

@Composable
private fun WeightPinnedCard(onClick: () -> Unit = {}) {
    PinnedCard("🧍", "Weight", titleColor = Color(0xFFFF2D55), "72.1", "kg", label = "Average", onClick = onClick) {
        MinimalLineChart(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            data = weightData,
            color = AHWeight,
            strokeWidth = 2f,
            showPoints = true
        )
    }
}

// ── Trend cards ───────────────────────────────────────────────────────────────

@Composable
private fun HeartRateTrendCard() {
    AHCardContainer {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("♥", fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Text("Resting Heart Rate", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AHHeartRate)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "On average, your resting heart rate was 69 BPM over the last 26 weeks.",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp
            )
        }
        CardHairline()
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            MinimalLineChart(
                modifier = Modifier.fillMaxSize(),
                data = hrTrendData,
                color = Color(0xFFD1D1D6),
                strokeWidth = 4f,
                padding = 0f,
                referenceLines = listOf(
                    ReferenceLineSpec(
                        type = ReferenceLineType.THRESHOLD, y = 69.0,
                        color = AHHeartRate, strokeWidth = 3.dp, style = LineStyle.SOLID
                    )
                )
            )
        }
        Text(
            "26 weeks", fontSize = 11.sp, color = AHHeartRate, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, bottom = 10.dp, top = 4.dp)
        )
    }
}

@Composable
private fun StepsTrendCard() {
    AHCardContainer {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Text("Steps", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AHSteps)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "On average, you took 8,189 steps per day over the past 26 weeks.",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp
            )
        }
        CardHairline()
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            MinimalBarChart(
                modifier = Modifier.fillMaxSize(),
                data = stepsTrendData,
                color = Color(0xFFD1D1D6),
                barWidthRatio = 0.55f,
                roundTopOnly = true,
                barCornerRadiusFraction = 0.1f,
                padding = 0f,
                referenceLines = listOf(
                    ReferenceLineSpec(
                        type = ReferenceLineType.THRESHOLD, y = 8189.0,
                        color = AHSteps, strokeWidth = 3.dp, style = LineStyle.SOLID
                    )
                )
            )
        }
        Text(
            "26 weeks", fontSize = 11.sp, color = AHSteps, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, bottom = 10.dp, top = 4.dp)
        )
    }
}

@Composable
private fun BloodOxygenTrendCard() {
    val skyBlue = Color(0xFF5AC8FA)
    val barGrey = Color(0xFFD1D1D6)
    val n = bloodOxygenBarMins.size

    val yDomainMax = 100.5
    val yDomainMin = 91.5
    val yRange = yDomainMax - yDomainMin

    AHCardContainer {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🫁", fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Blood Oxygen",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = skyBlue
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "On average, your blood oxygen level was 98.1% over the last 26 weeks.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        }
        CardHairline()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val slotW = size.width / n
                val barW = slotW * 0.45f
                val dotR = slotW * 0.20f

                fun valueToY(v: Double): Float =
                    ((yDomainMax - v) / yRange * size.height).toFloat()

                for (i in 0 until n) {
                    val cx = (i + 0.5f) * slotW
                    val topY = valueToY(100.0)
                    val botY = valueToY(bloodOxygenBarMins[i])
                    val h = (botY - topY).coerceAtLeast(4f)
                    val r = (barW / 2f).coerceAtMost(h / 2f)
                    drawRoundRect(
                        color = barGrey,
                        topLeft = Offset(cx - barW / 2f, topY),
                        size = Size(barW, h),
                        cornerRadius = CornerRadius(r)
                    )
                    for (dotVal in bloodOxygenDots[i]) {
                        drawCircle(
                            color = barGrey,
                            radius = dotR,
                            center = Offset(cx, valueToY(dotVal))
                        )
                    }
                }

                val refY = valueToY(95.0)
                drawLine(
                    color = skyBlue,
                    start = Offset(0f, refY),
                    end = Offset(size.width, refY),
                    strokeWidth = 7f
                )
            }
        }
        Text(
            "26 weeks",
            fontSize = 11.sp,
            color = skyBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, bottom = 10.dp, top = 4.dp)
        )
    }
}

// ── Highlight cards ───────────────────────────────────────────────────────────

@Composable
private fun StepsHighlightCard() {
    AHCardContainer {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Text("Steps", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AHSteps)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "You averaged 4,331 steps a day over the last 7 days.",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp
            )
        }
        CardHairline()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌: 상태 텍스트
            Column(modifier = Modifier.weight(1f)) {
                Text("Average Steps", fontSize = 11.sp, color = AHGray)
                Spacer(Modifier.height(4.dp))
                Text("4,331", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AHSteps)
                Text("steps/day", fontSize = 11.sp, color = AHGray)
            }
            // 우: 차트 + 요일 레이블
            Column(modifier = Modifier.weight(1.6f)) {
                MinimalBarChart(
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    data = stepsData,
                    color = Color(0xFFD1D1D6),
                    barWidthRatio = 0.55f,
                    roundTopOnly = true,
                    barCornerRadiusFraction = 0.15f,
                    padding = 0f,
                    referenceLines = listOf(
                        ReferenceLineSpec(
                            type = ReferenceLineType.THRESHOLD, y = 4331.0,
                            color = AHSteps, strokeWidth = 3.dp, style = LineStyle.SOLID
                        )
                    )
                )
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("T", "F", "S", "S", "M", "T", "W").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 10.sp,
                            color = AHGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseHighlightCard() {
    AHCardContainer {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Text("Activity", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RingMove)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "This week, you've closed all your rings 1 time.",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp
            )
        }
        CardHairline()
        data class DayRings(
            val day: String,
            val move: String, val exercise: String, val stand: String,
            val rings: List<ProgressChartMark>
        )

        val ringRows = listOf(
            DayRings(
                "Monday", "250", "20", "8",
                listOf(
                    ProgressChartMark(0.0, current = 250.0, max = 600.0, label = "Move", unit = "kcal"),
                    ProgressChartMark(1.0, current = 20.0, max = 60.0, label = "Exercise", unit = "min"),
                    ProgressChartMark(2.0, current = 8.0, max = 16.0, label = "Stand", unit = "hr"),
                )
            ),
            DayRings(
                "Tuesday", "380", "30", "11",
                listOf(
                    ProgressChartMark(0.0, current = 380.0, max = 600.0, label = "Move", unit = "kcal"),
                    ProgressChartMark(1.0, current = 30.0, max = 60.0, label = "Exercise", unit = "min"),
                    ProgressChartMark(2.0, current = 11.0, max = 16.0, label = "Stand", unit = "hr"),
                )
            ),
            DayRings("Wednesday", "600", "41", "13", activityRings),
            DayRings(
                "Thursday", "10", "1", "0",
                listOf(
                    ProgressChartMark(0.0, current = 10.0, max = 600.0, label = "Move", unit = "kcal"),
                    ProgressChartMark(1.0, current = 1.0, max = 60.0, label = "Exercise", unit = "min"),
                    ProgressChartMark(2.0, current = 0.0, max = 16.0, label = "Stand", unit = "hr"),
                )
            ),
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ringRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        MiniActivityRings(
                            modifier = Modifier.size(28.dp),
                            rings = row.rings,
                            colors = ringColors,
                            strokeWidth = 8f
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(row.day, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.width(48.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ArrowForward, null, tint = RingMove, modifier = Modifier.size(11.dp))
                        Text(row.move, fontSize = 12.sp, color = RingMove)
                    }
                    Row(modifier = Modifier.width(48.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ArrowForward, null, tint = RingExercise, modifier = Modifier.size(11.dp))
                        Text(row.exercise, fontSize = 12.sp, color = RingExercise)
                    }
                    Row(modifier = Modifier.width(48.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ArrowForward, null, tint = RingStand, modifier = Modifier.size(11.dp))
                        Text(row.stand, fontSize = 12.sp, color = RingStand)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnvironmentalSoundLevelsHighlightCard() {
    val averageLevel = 71.0

    AHCardContainer {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔊", fontSize = 13.sp, color = AHBlue)
                Spacer(Modifier.width(6.dp))
                Text("Environmental Sound Levels", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AHBlue)
                Spacer(Modifier.weight(1f))
                Text("›", fontSize = 18.sp, color = AHGray)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Your average environmental sound levels were OK over the last seven days.",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp
            )
        }
        CardHairline()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌: 상태 텍스트
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(AHGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(5.dp))
                    Text("OK", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Text("77 dB for 1 day", fontSize = 11.sp, color = AHGray)
                Text("6 hr", fontSize = 11.sp, color = AHGray)
            }
            // 우: 차트 + 레이블
            Column(modifier = Modifier.weight(1.6f)) {
                EnvironmentalSoundLevelsChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    data = environmentalSoundLevelsData,
                    averageLevel = averageLevel
                )
                Spacer(Modifier.height(4.dp))
                val barPaddingDp = with(LocalDensity.current) { 30f.toDp() }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = barPaddingDp)) {
                    listOf("T", "F", "S", "S", "M", "T", "W").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 10.sp,
                            color = AHGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnvironmentalSoundLevelsChart(
    modifier: Modifier = Modifier,
    data: List<ChartMark>,
    averageLevel: Double,
) {
    val minY = remember(data) { data.minOf { it.y } }
    val maxY = remember(data) { data.maxOf { it.y } }

    Box(modifier = modifier) {
        MinimalRangeBarChart(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp),
            data = data,
            color = AHChartGray,
            barWidthRatio = 0.26f,
            barCornerRadiusFraction = 0.5f,
            roundTopOnly = false
        )
        Text(
            text = "Average Level",
            fontSize = 11.sp,
            color = AHGray,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp)
        ) {
            val yRatio = ((maxY - averageLevel) / (maxY - minY)).toFloat()
            val y = size.height * yRatio.coerceIn(0f, 1f)
            drawLine(
                color = AHBlue,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 6f
            )
        }
    }
}

// ── Pinned detail screens ─────────────────────────────────────────────────────

@Composable
private fun PinnedDetailScreen(detail: PinnedDetail, onBack: () -> Unit) {
    Scaffold(containerColor = AHBackground) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AHChartGray)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                }
                Text(
                    text = when (detail) {
                        PinnedDetail.ACTIVITY   -> "Activity"
                        PinnedDetail.HRV        -> "Heart Rate Variability"
                        PinnedDetail.HEART_RATE -> "Resting Heart Rate"
                        PinnedDetail.SLEEP      -> "Sleep"
                        PinnedDetail.STEPS      -> "Steps"
                        PinnedDetail.WEIGHT     -> "Weight"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AHChartGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("···", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            when (detail) {
                PinnedDetail.ACTIVITY   -> ActivityDetailContent()
                PinnedDetail.HRV        -> HRVDetailContent()
                PinnedDetail.HEART_RATE -> HeartRateDetailContent()
                PinnedDetail.SLEEP      -> SleepDetailContent()
                PinnedDetail.STEPS      -> StepsDetailContent()
                PinnedDetail.WEIGHT     -> WeightDetailContent()
            }
        }
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun PeriodSelector(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    val periods = listOf("H", "D", "W", "M", "6M", "Y")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFFE5E5EA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            periods.forEach { period ->
                val isSelected = period == selectedPeriod
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        period,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else AHGray
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepPeriodSelector(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    val periods = listOf("D", "W", "M", "6M")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFFE5E5EA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            periods.forEach { period ->
                val isSelected = period == selectedPeriod
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        period,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else AHGray
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityPeriodSelector(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    val periods = listOf("D", "W", "M", "6M", "Y")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFFE5E5EA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            periods.forEach { period ->
                val isSelected = period == selectedPeriod
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        period,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else AHGray
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityMetricBarSection(
    title: String,
    dayCount: String,
    unit: String,
    color: Color,
    data: List<ChartMark>,
    goalY: Double,
    goalTopFraction: Float,
    chartHeight: Int = 110
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color)
            Spacer(Modifier.weight(1f))
            Text(dayCount, fontSize = 13.sp, color = AHGray)
        }
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(chartHeight.dp)) {
            MinimalBarChart(
                modifier = Modifier.fillMaxWidth().height(chartHeight.dp),
                data = data,
                color = color,
                barWidthRatio = 0.45f,
                roundTopOnly = true,
                barCornerRadiusFraction = 0.12f,
                padding = 0f,
                referenceLines = listOf(
                    ReferenceLineSpec(
                        type = ReferenceLineType.THRESHOLD,
                        y = goalY,
                        color = color,
                        strokeWidth = 1.5.dp,
                        style = LineStyle.DASHED
                    )
                )
            )
            Text(
                "Goal",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = (chartHeight * goalTopFraction).dp, end = 4.dp),
                fontSize = 10.sp,
                color = color
            )
            Text(
                "0 $unit",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 2.dp, end = 4.dp),
                fontSize = 10.sp,
                color = AHGray
            )
        }
    }
}

@Composable
private fun ActivityDetailContent() {
    var selectedPeriod by remember { mutableStateOf("M") }

    ActivityPeriodSelector(selectedPeriod) { selectedPeriod = it }

    // Summary legend row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        listOf(
            Triple("Move", "420 kcal", RingMove),
            Triple("Exercise", "41 min", RingExercise),
            Triple("Stand", "13 hr", RingStand)
        ).forEach { (label, value, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
                    Text(value, fontSize = 11.sp, color = AHGray)
                }
            }
        }
    }

    // Three metric chart sections in one card
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Move chart (goal=600 kcal, max≈790 → goalTopFraction≈0.24)
        ActivityMetricBarSection(
            title = "Move",
            dayCount = "19 of 31 days",
            unit = "kcal",
            color = RingMove,
            data = activityMoveMonthly,
            goalY = 600.0,
            goalTopFraction = 0.24f
        )
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AHSeparator))
        Spacer(Modifier.height(12.dp))

        // Exercise chart (goal=30 min, max≈45 → goalTopFraction≈0.35)
        ActivityMetricBarSection(
            title = "Exercise",
            dayCount = "3 of 31 days",
            unit = "min",
            color = RingExercise,
            data = activityExerciseMonthly,
            goalY = 30.0,
            goalTopFraction = 0.35f
        )
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AHSeparator))
        Spacer(Modifier.height(12.dp))

        // Stand chart (goal=12 hr, max≈16 → goalTopFraction≈0.25)
        ActivityMetricBarSection(
            title = "Stand",
            dayCount = "22 of 31 days",
            unit = "hrs",
            color = RingStand,
            data = activityStandMonthly,
            goalY = 12.0,
            goalTopFraction = 0.25f
        )
        Spacer(Modifier.height(6.dp))

        // Shared x-axis date labels
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("24", fontSize = 10.sp, color = AHGray)
            Spacer(Modifier.weight(1f))
            Text("3", fontSize = 10.sp, color = AHGray, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Text("10", fontSize = 10.sp, color = AHGray, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Text("17", fontSize = 10.sp, color = AHGray)
        }
    }

    // About Activity card
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(16.dp)
    ) {
        Text(
            "About Activity",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Activity rings give you a quick visual reference of how active you are each day. When you complete your daily goal, its ring closes.",
            fontSize = 14.sp,
            color = Color(0xFF3C3C43),
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "iPhone tracks your activity with a Move ring, which measures the number of active calories you burn.",
            fontSize = 14.sp,
            color = Color(0xFF3C3C43),
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "In addition to a Move ring, Apple Watch tracks activity with Exercise and Stand rings. Exercise measures how many minutes of brisk activity you do. Stand measures how often you stand up and move round throughout the day.",
            fontSize = 14.sp,
            color = Color(0xFF3C3C43),
            lineHeight = 20.sp
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun HRVDetailContent() {
    var selectedPeriod by remember { mutableStateOf("W") }

    PeriodSelector(selectedPeriod) { selectedPeriod = it }

    // Chart card
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(16.dp)
    ) {
        Text("AVERAGE", fontSize = 12.sp, color = AHGray, fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("45", fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Text("ms", fontSize = 15.sp, color = AHGray, modifier = Modifier.padding(bottom = 4.dp))
        }
        Text("Apr 19–25, 2025", fontSize = 13.sp, color = AHGray)
        Spacer(Modifier.height(8.dp))
        LineChart(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            data = hrvData,
            lineColor = AHHRV,
            strokeWidth = 3f,
            showTitle = false,
            showYAxis = true,
            yAxisPosition = YAxisPosition.LEFT,
            showPoint = true,
            xLabelAutoSkip = false,
            contentPadding = PaddingValues(0.dp),
            showLegend = false,
        )
    }

    // Latest row
    Spacer(Modifier.height(8.dp))
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Latest: Yesterday", fontSize = 15.sp, modifier = Modifier.weight(1f))
            Text("45 ms", fontSize = 15.sp, color = AHGray)
            Spacer(Modifier.width(8.dp))
            Text("›", fontSize = 18.sp, color = AHGray)
        }
    }

    // Show More link
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Show More Heart Rate Variability Data", fontSize = 15.sp, color = AHBlue)
    }

    // About section
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(16.dp)
    ) {
        Text("About Heart Rate Variability", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Heart rate variability (HRV) is the variation in time between each heartbeat. " +
            "A higher HRV generally indicates better cardiovascular fitness and recovery. " +
            "HRV is influenced by factors like stress, sleep, exercise, and overall health.",
            fontSize = 13.sp,
            color = AHGray,
            lineHeight = 18.sp
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun HeartRateDetailContent() {
    var selectedPeriod by remember { mutableStateOf("W") }

    val heartRateData = remember { getHeartRateData() }
    val allSamples = remember(heartRateData) { heartRateData.flatMap { it.samples } }
    val minBPM = remember(allSamples) { allSamples.minOfOrNull { it.beatsPerMinute } ?: 0 }
    val maxBPM = remember(allSamples) { allSamples.maxOfOrNull { it.beatsPerMinute } ?: 0 }

    // ── Weekly (W): daily IQR bars with day-of-week labels ───────────────────
    val dayBaseMarks: List<BaseChartMark> = remember(heartRateData) {
        heartRateData.transform(timeUnit = TimeUnitGroup.DAY, aggregationType = AggregationType.MIN_MAX)
    }
    val dayBaseRangeMarks = remember(dayBaseMarks) { dayBaseMarks.filterIsInstance<RangeChartMark>() }

    val englishLabels = remember(heartRateData) {
        val zone = ZoneId.systemDefault()
        heartRateData.map { hr ->
            when (LocalDateTime.ofInstant(hr.startTime, zone).dayOfWeek) {
                DayOfWeek.MONDAY    -> "Mon"
                DayOfWeek.TUESDAY   -> "Tue"
                DayOfWeek.WEDNESDAY -> "Wed"
                DayOfWeek.THURSDAY  -> "Thu"
                DayOfWeek.FRIDAY    -> "Fri"
                DayOfWeek.SATURDAY  -> "Sat"
                DayOfWeek.SUNDAY    -> "Sun"
            }
        }
    }

    val dailyValues = remember(heartRateData, dayBaseRangeMarks) {
        List(dayBaseRangeMarks.size) { i ->
            heartRateData.getOrNull(i)
                ?.samples?.map { it.beatsPerMinute.toDouble() }?.sorted()
                ?: emptyList()
        }
    }

    val weeklyRangeMarks = remember(dayBaseRangeMarks, dailyValues, englishLabels) {
        dayBaseRangeMarks.mapIndexed { i, mark ->
            val v = dailyValues[i]
            val label = englishLabels.getOrNull(i) ?: mark.label
            if (v.size < 2) return@mapIndexed mark.copy(label = label)
            val q1 = v[v.size / 4]
            val q3 = v[3 * v.size / 4]
            if (q1 >= q3) return@mapIndexed mark.copy(label = label)
            RangeChartMark(x = mark.x, minPoint = ChartMark(x = mark.x, y = q1),
                maxPoint = ChartMark(x = mark.x, y = q3), label = label)
        }
    }

    val weeklyPointValues = remember(dayBaseRangeMarks, dailyValues) {
        dayBaseRangeMarks.mapIndexed { i, _ ->
            val v = dailyValues[i]
            if (v.size < 2) return@mapIndexed v
            val q1 = v[v.size / 4]; val q3 = v[3 * v.size / 4]
            v.filter { it < q1 || it > q3 }
        }
    }

    // ── Hourly (H): hourly IQR bars ──────────────────────────────────────────
    val hourBaseMarks: List<BaseChartMark> = remember(heartRateData) {
        heartRateData.transform(timeUnit = TimeUnitGroup.HOUR, aggregationType = AggregationType.MIN_MAX)
    }
    val hourBaseRangeMarks = remember(hourBaseMarks) { hourBaseMarks.filterIsInstance<RangeChartMark>() }

    val hourlyValues = remember(allSamples, hourBaseRangeMarks) {
        val sortedSamples = allSamples.sortedBy { it.time }
        val grouped = sortedSamples
            .mapIndexed { index, sample -> (index / 4) to sample.beatsPerMinute.toDouble() }
            .groupBy({ it.first }, { it.second })
        List(hourBaseRangeMarks.size) { hourIndex ->
            grouped[hourIndex]?.sorted() ?: emptyList()
        }
    }

    val hourlyRangeMarks = remember(hourBaseRangeMarks, hourlyValues) {
        hourBaseRangeMarks.mapIndexed { i, mark ->
            val v = hourlyValues[i]
            if (v.size < 2) return@mapIndexed mark
            val q1 = v[v.size / 4]
            val q3 = v[3 * v.size / 4]
            if (q1 >= q3) return@mapIndexed mark
            RangeChartMark(x = mark.x, minPoint = ChartMark(x = mark.x, y = q1),
                maxPoint = ChartMark(x = mark.x, y = q3), label = mark.label)
        }
    }

    val hourlyPointValues = remember(hourBaseRangeMarks, hourlyValues) {
        hourBaseRangeMarks.mapIndexed { i, _ ->
            val v = hourlyValues[i]
            if (v.size < 2) return@mapIndexed v
            val q1 = v[v.size / 4]; val q3 = v[3 * v.size / 4]
            v.filter { it < q1 || it > q3 }
        }
    }

    // ── Active data depending on selected period ─────────────────────────────
    val isHourly = selectedPeriod == "H"
    val activeRangeMarks = if (isHourly) hourlyRangeMarks else weeklyRangeMarks
    val activePointValues = if (isHourly) hourlyPointValues else weeklyPointValues
    val activePageSize = if (isHourly) 24 else 7

    PeriodSelector(selectedPeriod) { selectedPeriod = it }

    // Chart card
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            Text("RANGE", fontSize = 12.sp, color = AHGray, fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$minBPM–$maxBPM", fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                Text("BPM", fontSize = 15.sp, color = AHGray, modifier = Modifier.padding(bottom = 4.dp))
            }
            Text("Jul 25–31, 2025", fontSize = 13.sp, color = AHGray)
            Spacer(Modifier.height(8.dp))
        }
        RangeBarChart(
            modifier = Modifier.fillMaxWidth().height(260.dp),
            data = activeRangeMarks,
            barColor = AHHeartRate,
            barWidthRatio = 0.15f,
            barCornerRadiusFraction = 0.5f,
            roundTopOnly = false,
            showTitle = false,
            yAxisPosition = YAxisPosition.RIGHT,
            showLegend = false,
            interactionType = InteractionType.RangeBar.TOUCH_AREA,
            unit = "bpm",
            pageSize = activePageSize,
            contentPadding = PaddingValues(0.dp),
            pointValues = activePointValues,
            pointColor = AHHeartRate,
            pointRadius = 3.dp,
        )
        Spacer(Modifier.height(8.dp))
    }

    // Latest row
    Spacer(Modifier.height(8.dp))
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Latest: Aug 4", fontSize = 15.sp, modifier = Modifier.weight(1f))
            Text("70 BPM", fontSize = 15.sp, color = AHGray)
            Spacer(Modifier.width(8.dp))
            Text("›", fontSize = 18.sp, color = AHGray)
        }
    }

    // Show More link
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Show More Heart Rate Data", fontSize = 15.sp, color = AHBlue)
    }

    // About section
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(16.dp)
    ) {
        Text("About Resting Heart Rate", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Your resting heart rate is the number of times your heart beats per minute when " +
            "you're at rest. A normal resting heart rate for adults ranges from 60 to 100 beats " +
            "per minute. Generally, a lower resting heart rate means more efficient heart function " +
            "and better cardiovascular fitness.",
            fontSize = 13.sp,
            color = AHGray,
            lineHeight = 18.sp
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun SleepDetailContent() {
    var selectedPeriod by remember { mutableStateOf("D") }
    var showSleepFocusCard by remember { mutableStateOf(true) }

    SleepPeriodSelector(selectedPeriod) { selectedPeriod = it }

    // Main chart card
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "TIME ASLEEP",
                    fontSize = 12.sp,
                    color = AHGray,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("7", fontSize = 44.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "hr",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("33", fontSize = 44.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "min",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text("Aug 11, 2025", fontSize = 13.sp, color = AHGray)
            }
            // Info (i) button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, AHGray.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("i", fontSize = 13.sp, color = AHGray, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(16.dp))
        CompositionLocalProvider(LocalSalusChartColors provides sleepDetailColorScheme) {
            SleepStageChart(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                sleepSession = sleepDetailSession,
                showTitle = false,
                showYAxis = true,
                showXAxis = true,
                showStartEndLabels = true,
                contentPadding = PaddingValues(0.dp),
            )
        }
    }

    // Show More Sleep Data
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Show More Sleep Data", fontSize = 15.sp, color = AHBlue)
    }

    // Customize Sleep Focus card
    if (showSleepFocusCard) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AHCard)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AHSleep.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛏", fontSize = 26.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Customize Sleep Focus",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "You can choose people or apps that can reach you while you're sleeping.",
                        fontSize = 13.sp,
                        color = AHGray,
                        lineHeight = 18.sp
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "×",
                    fontSize = 18.sp,
                    color = AHGray,
                    modifier = Modifier
                        .clickable { showSleepFocusCard = false }
                        .padding(4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text("Customize Focus", fontSize = 15.sp, color = AHBlue)
        }
    }

    // Highlights section header
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Highlights",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text("Show All", fontSize = 15.sp, color = AHBlue)
    }
    Spacer(Modifier.height(8.dp))

    // Highlight card
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AHCard)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🛏", fontSize = 14.sp)
            Spacer(Modifier.width(5.dp))
            Text("Sleep", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AHSleep)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "The past seven days, the typical level of deep sleep was 1 hour 12 minutes per night, which was typical for you.",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun StepsDetailContent() {
    DetailCard {
        Text("Last 7 Days", fontSize = 13.sp, color = AHGray)
        Row(verticalAlignment = Alignment.Bottom) {
            Text("9,340", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Text("steps", fontSize = 15.sp, color = AHGray, modifier = Modifier.padding(bottom = 5.dp))
        }
        Text("Peak", fontSize = 13.sp, color = AHGray)
        Spacer(Modifier.height(16.dp))
        MinimalBarChart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            data = stepsData,
            color = AHSteps,
            barWidthRatio = 0.55f,
            roundTopOnly = true,
            barCornerRadiusFraction = 0.1f,
            padding = 0f,
        )
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(day, fontSize = 10.sp, color = AHGray, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WeightDetailContent() {
    DetailCard {
        Text("Last 7 Days", fontSize = 13.sp, color = AHGray)
        Row(verticalAlignment = Alignment.Bottom) {
            Text("72.1", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Text("kg", fontSize = 15.sp, color = AHGray, modifier = Modifier.padding(bottom = 5.dp))
        }
        Spacer(Modifier.height(16.dp))
        MinimalLineChart(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            data = weightData,
            color = AHWeight,
            strokeWidth = 3f,
            padding = 8f,
            showPoints = true,
        )
    }
}
