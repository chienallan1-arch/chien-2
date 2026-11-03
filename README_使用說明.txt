【小米 LINE 通話自動錄音 (MIUI / HyperOS 專用版) 使用說明】

1. 如何免安裝 Android Studio 直接獲得 APK？
   - 將本壓縮包解壓縮後，上傳至你的 GitHub 儲存庫 (Repository)。
   - 點擊 GitHub 頁面上的「Actions」分頁，系統會自動透過 .github/workflows/build-apk.yml 進行雲端打包。
   - 約 1~2 分鐘完成後，在 Artifacts 區塊即可直接下載編譯好的 app-debug.apk！

2. 如何啟用小米隱藏入口與免 Root 自動觸發？
   - 將手機開啟「開發者選項 -> USB 偵錯」並連接電腦。
   - 點擊兩下執行本目錄中的「setup_xiaomi_line_rec.bat」。
   - 腳本會自動幫你安裝 APK、開啟小米應用錄音隱藏開關，並授權通知狀態監聽。

3. 錄音檔存在哪裡？
   - 手機內建儲存空間：MIUI/sound_recorder/app_rec/
   - 本 App 完全無 INTERNET 網路權限，不連網、不讀取 LINE 聊天內容，雙方聲音皆清楚收錄且不會在對方 LINE 顯示正在錄音。