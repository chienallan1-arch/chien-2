package com.example.lineautorecorder

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LineCallMonitorService : NotificationListenerService() {

    private lateinit var audioManager: AudioManager
    private val handler = Handler(Looper.getMainLooper())
    private var isLineCallActive = false
    private var isRecording = false
    private var fallbackRecorder: MediaRecorder? = null

    // 接通滿 3 秒後才啟動錄音（避免響鈴或誤觸）
    private val startRecordRunnable = Runnable {
        if (isLineCallActive && !isRecording && isInVoipCallMode()) {
            startXiaomiAppRecOrFallback()
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn?.packageName != "jp.naver.line.android") return

        // 只檢查 Notification Category 是否為通話 (CATEGORY_CALL)，絕不讀取訊息內文
        val category = sbn.notification.category
        val isOngoing = (sbn.notification.flags and Notification.FLAG_ONGOING_EVENT) != 0

        if (category == Notification.CATEGORY_CALL && isOngoing) {
            // 確認系統音訊進入 MODE_IN_COMMUNICATION (代表已接通，而非響鈴中)
            if (!isLineCallActive && isInVoipCallMode()) {
                isLineCallActive = true
                handler.removeCallbacks(startRecordRunnable)
                handler.postDelayed(startRecordRunnable, 3000L) // 接通約 3 秒自動開始錄
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn?.packageName != "jp.naver.line.android") return
        if (isLineCallActive) {
            isLineCallActive = false
            handler.removeCallbacks(startRecordRunnable)
            if (isRecording) {
                stopXiaomiAppRec()
            }
        }
    }

    private fun isInVoipCallMode(): Boolean {
        return audioManager.mode == AudioManager.MODE_IN_COMMUNICATION
    }

    /**
     * 優先喚醒小米內建錄音程式隱藏入口，將雙向錄音存入 MIUI/sound_recorder/app_rec/
     */
    private fun startXiaomiAppRecOrFallback() {
        isRecording = true
        try {
            val intent = Intent("com.android.soundrecorder.START_APP_RECORD").apply {
                component = ComponentName(
                    "com.android.soundrecorder",
                    "com.android.soundrecorder.RecorderService"
                )
                putExtra("package_name", "jp.naver.line.android")
                putExtra("audio_source", MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            }
            startService(intent)
        } catch (e: Exception) {
            // 若非標準服務名稱，直接寫入 MIUI/sound_recorder/app_rec/ 目錄
            startDirectMiuiFolderRecorder()
        }
    }

    private fun startDirectMiuiFolderRecorder() {
        val dir = File(
            Environment.getExternalStorageDirectory(),
            "MIUI/sound_recorder/app_rec"
        )
        if (!dir.exists()) dir.mkdirs()

        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outFile = File(dir, "LINE_${stamp}.m4a")

        fallbackRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(outFile.absolutePath)
            prepare()
            start()
        }
    }

    private fun stopXiaomiAppRec() {
        isRecording = false
        try {
            val intent = Intent("com.android.soundrecorder.STOP_APP_RECORD").apply {
                component = ComponentName(
                    "com.android.soundrecorder",
                    "com.android.soundrecorder.RecorderService"
                )
            }
            startService(intent)
        } catch (_: Exception) {}

        fallbackRecorder?.apply {
            try { stop() } catch (_: Exception) {}
            release()
        }
        fallbackRecorder = null
    }
}