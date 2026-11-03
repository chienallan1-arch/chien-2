@echo off
echo ===================================================
echo   Xiaomi MIUI / HyperOS LINE Auto-Recorder Setup
echo ===================================================
echo.
echo [1/4] Checking ADB device connection...
adb devices
echo.
echo [2/4] Unlocking Xiaomi App Call Recorder hidden settings...
adb shell appops set com.android.shell WRITE_SETTINGS allow
adb shell settings put system call_record_app_support 1
adb shell settings put System call_rec_app_switch 1
adb shell settings put System call_rec_custom_app_switch 1
echo.
echo [3/4] Installing APK if app-debug.apk exists in folder...
if exist "app-debug.apk" (
    adb install -r "app-debug.apk"
)
echo.
echo [4/4] Granting permissions to Recorder Helper App...
adb shell pm grant com.example.lineautorecorder android.permission.RECORD_AUDIO
adb shell appops set com.example.lineautorecorder RECORD_AUDIO allow
adb shell cmd notification allow_listener com.example.lineautorecorder/.LineCallMonitorService
echo.
echo ===================================================
echo   Done! You can now unplug your phone.
echo ===================================================
pause