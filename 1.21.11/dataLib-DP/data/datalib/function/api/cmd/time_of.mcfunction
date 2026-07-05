execute unless function datalib:debug/tools/utils/check_all run return 0

# DL - Universal World Clock Controller
# Usage: /function ame:clock_handler {clock:"datalib:test", action:"set", value:"12000"}
# NOT UYUMLU: '/time of <clock> ...' syntax'i World Clock sistemiyle (26.1+) geldi.
# 1.21.11'de bu komut yok, calistirilirsa "Unparseable" hatasi doner.
# Bu fonksiyon su an hicbir yerden cagrilmiyor (izole), o yuzden silinmedi.
# 1.21.11 icin gercek karsiligi yok; 26.1+'a tekrar gecmeden aktif etmeyin.
$time of $(clock) $(action) $(value)

# System Debug Log for staff (Only for users with 'datalib.debug' tag)
$tellraw @a[tag=datalib.debug] ["",{"text":"[DL] ","color":"#00AAAA","bold":true},{"text":"clock/system_update ","color":"aqua"},{"text":"$(clock) ","color":"white"},{"text":"action:","color":"gray"},{"text":"$(action) ","color":"gold"},{"text":"value:","color":"gray"},{"text":"$(value)","color":"yellow"}]