#> This is the main function, that will run once per tick
execute run function datalib:core/tick
function #datalib:loop

execute as @a[scores={dl.reload=1..}] at @s run function dl_load:reload/main
execute as @a[scores={dl.reload=1..}] at @s run scoreboard players set @s dl.reload 0
execute as @a[scores={dl.reload=-21416..0}] at @s run scoreboard players enable @s dl.reload