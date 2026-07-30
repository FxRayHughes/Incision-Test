rootProject.name = "Incision-Test"

// 第二个插件必须独立构建，才能在服务端产生真实的第二套 TabooLib/Incision ClassLoader。
include("bridge-peer")
