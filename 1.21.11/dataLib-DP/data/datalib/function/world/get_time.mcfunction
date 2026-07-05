# ─────────────────────────────────────────────────────────────────
# datalib:world/get_time
# Compatible with 1.21.11 (pre-World Clock /time syntax)
#
# OUTPUT:
# datalib:output.daytime → in-day time (0-23999)
# datalib:output.total → total world age (gametime)
# datalib:output.day → current day number
# ─────────────────────────────────────────────────────────────────

execute store result storage datalib:output daytime int 1 run time query daytime
execute store result storage datalib:output total int 1 run time query gametime
execute store result storage datalib:output day int 1 run time query day

# Debug (optional)
tellraw @a[tag=datalib.debug] ["",{"text":"[DL] ","color":"#00AAAA","bold":true},{"text":"world/get_time ","color":"aqua"},{"text":"day=","color":"gray"},{"storage":"datalib:output","nbt":"day","color":"green"},{"text":" daytime=","color":"gray"},{"storage":"datalib:output","nbt":"daytime","color":"green"},{"text":" total=","color":"gray"},{"storage":"datalib:output","nbt":"total","color":"green"}]
