package me.lightless.bot.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object PubgSeasonDetailModel : LongIdTable("pubg_season_detail") {

    val season = text("season")
    val playerId = varchar("player_id", 64).index()

    val assists = integer("assists")
    val dbnos = integer("dbnos")
    val kills = integer("kills")
    val damageDealt = float("damage_dealt")   // 伤害总量
    val longestKill = float("longest_kill")   // 最远击杀
    val headshotKills = integer("headshot_kills")   // 爆头击杀
    val maxKillStreaks = integer("max_kill_streaks")    // 最大连杀
    val roundMostKills = integer("round_most_kills")    // 一局中的最大击杀次数
    val revives = integer("revives")    // 摸队友次数
    val heals = integer("heals")        // 治疗次数
    val boosts = integer("boosts")      // 喝药次数
    val teamKills = integer("team_kills")   // 灭队次数
    val roadKills = integer("road_kills")   // 马路杀手次数
    val roundsPlayed = integer("rounds_played") // 一共玩了多少局
    val suicides = integer("suicides")  // 自杀次数
    val vehicleDestroys = integer("vehicle_destroy")    // 破坏车辆次数

    val rideDistance = float("ride_distance")   // 开车距离
    val swimDistance = float("swim_distance")   // 游泳距离
    val walkDistance = float("walk_distance")   // 徒步次数

    val wins = integer("wins")  // 吃鸡次数
    val top10s = integer("top10s")  // 前10次数

    val weeklyKills = integer("weekly_kills")   // 本周击杀
    val weeklyWins = integer("weekly_wins")     // 本周吃鸡

    val createdTime = datetime("created_time").default(DateTime.now())
    val updatedTime = datetime("updated_time").default(DateTime.now())
}


class PubgSeasonDetailDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PubgSeasonDetailDAO>(PubgSeasonDetailModel)

    var season by PubgSeasonDetailModel.season
    var playerId by PubgSeasonDetailModel.playerId

    var assists by PubgSeasonDetailModel.assists
    var dbnos by PubgSeasonDetailModel.dbnos
    var kills by PubgSeasonDetailModel.kills
    var damageDealt by PubgSeasonDetailModel.damageDealt
    var longestKill by PubgSeasonDetailModel.longestKill
    var headshotKills by PubgSeasonDetailModel.headshotKills
    var maxKillStreaks by PubgSeasonDetailModel.maxKillStreaks
    var roundMostKills by PubgSeasonDetailModel.roundMostKills
    var revives by PubgSeasonDetailModel.revives
    var heals by PubgSeasonDetailModel.heals
    var boosts by PubgSeasonDetailModel.boosts
    var teamKills by PubgSeasonDetailModel.teamKills
    var roadKills by PubgSeasonDetailModel.roadKills
    var roundsPlayed by PubgSeasonDetailModel.roundsPlayed
    var suicides by PubgSeasonDetailModel.suicides
    var vehicleDestroys by PubgSeasonDetailModel.vehicleDestroys

    var rideDistance by PubgSeasonDetailModel.rideDistance
    var swimDistance by PubgSeasonDetailModel.swimDistance
    var walkDistance by PubgSeasonDetailModel.walkDistance

    var wins by PubgSeasonDetailModel.wins
    var top10s by PubgSeasonDetailModel.top10s

    var weeklyKills by PubgSeasonDetailModel.weeklyKills
    var weeklyWins by PubgSeasonDetailModel.weeklyWins

    var createdTime by PubgSeasonDetailModel.createdTime
    var updatedTime by PubgSeasonDetailModel.updatedTime
}