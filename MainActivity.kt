package com.example.lineautorecorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    private lateinit var btnAuth: Button
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0B0F17"))
            setPadding(64, 96, 64, 64)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        title = TextView(this).apply {
            text = "LINE 通話錄音 (V1.2 診斷版)"
            textSize = 22f
            setTextColor(Color.parseColor("#06C755"))
        }

        val desc = TextView(this).apply {
            text = "\n• 狀態：背景監聽服務待命中\n" +
                   "• 觸發條件：LINE 接通滿 3 秒自動開始錄音\n" +
                   "• 停止條件：LINE 掛斷立即停止並存檔\n" +
                   "• 儲存位置：優先 MIUI/app_rec，失敗則存於 App 專屬 Music 資料夾\n" +
                   "• 隱私安全：本 App 無網路權限，不讀取訊息內容\n"
            textSize = 14f
            setTextColor(Color.parseColor("#E2E8F0"))
        }

        btnAuth = Button(this).apply {
            text = "檢查/開啟「通知存取權限」"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
        
        val hint = TextView(this).apply {
            text = "※ 若開關反灰顯示「受限設定」，請至手機設定 > 應用程式 > 本App > 點右上角「允許受限設定」即可解鎖。"
            textSize = 12f
            setTextColor(Color.parseColor("#EF4444"))
            setPadding(0, 32, 0, 0)
        }

        layout.addView(title)
        layout.addView(desc)
        layout.addView(btnAuth)
        layout.addView(hint)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        // 動態檢查通知權限是否已開啟，並更新 UI 狀態
        val listeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val isGranted = listeners != null && listeners.contains(packageName)
        
        if (isGranted) {
            title.text = "✅ 服務已啟動，背景待命中"
            btnAuth.text = "權限已開啟 (點此重新檢查)"
            btnAuth.setTextColor(Color.parseColor("#94a3b8"))
        } else {
            title.text = "❌ 請先開啟通知權限"
            title.setTextColor(Color.parseColor("#EF4444"))
            btnAuth.text = "點此開啟「通知存取權限」"
            btnAuth.setTextColor(Color.BLACK)
        }
    }
}