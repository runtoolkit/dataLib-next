# ======================================================================
# datalib:debug/tools/utils/check_all
# ======================================================================
#
# PURPOSE:
#   Tum sistem butunlugu kontrollerini sirayla calistirir. Herhangi biri
#   basarisiz olursa zincir hemen durur (fail-fast), kalan kontroller
#   calistirilmaz.
#
# KONTROL SIRASI VE GEREKCESI:
#   1. load_check   -- motor yuklenmemisse hicbir sey guvenilir degildir,
#                       once bu kontrol edilir.
#   2. perm_check    -- yukleme dogrulandiktan sonra yetki kontrolu yapilir.
#                       Yuklenmemis bir motorda yetki kontrolu anlamsizdir.
#   3. input_check   -- en pahali/en detayli kontrol en sona birakilir,
#                       cunku ilk ikisi zaten basarisizsa buraya hic
#                       gelinmemis olur (performans: gereksiz calisma yok).
#
# HATA IZLEME (bu fix ile eklendi):
#   Hangi kontrolun basarisiz oldugu datalib:debug last_failed_check
#   storage'ina yaziliyor. Bu, "check_all basarisiz oldu ama neden?"
#   sorusuna production ortaminda bile cevap verebilmek icin var --
#   onceden sadece "return 0" donuyordu, hangi asamada durduguna dair
#   hicbir iz birakmiyordu. Maliyeti: basarisiz olan asamada tek bir
#   data modify komutu, ihmal edilebilir.
#
#   Basarili durumda last_failed_check kaldirilir (onceki bir basarisiz
#   denemeden kalma stale veri onlenir).
#
# DONUS DEGERLERI:
#   return 1 -> tum kontroller gecti
#   return 0 -> en az bir kontrol basarisiz oldu (detay icin
#               datalib:debug last_failed_check'e bak)
#
# ======================================================================

execute unless function datalib:debug/tools/utils/load_check run data modify storage datalib:debug last_failed_check set value "load_check"
execute unless function datalib:debug/tools/utils/load_check run return 0

execute unless function datalib:debug/tools/utils/perm_check run data modify storage datalib:debug last_failed_check set value "perm_check"
execute unless function datalib:debug/tools/utils/perm_check run return 0

execute unless function datalib:debug/tools/utils/input_check run data modify storage datalib:debug last_failed_check set value "input_check"
execute unless function datalib:debug/tools/utils/input_check run return 0

data remove storage datalib:debug last_failed_check
return 1
