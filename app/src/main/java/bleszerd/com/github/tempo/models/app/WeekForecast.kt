package bleszerd.com.github.tempo.models.app

data class WeekForecast(
    var date: String,
    var condition: String,
    var minTemp: String,
    var maxTemp: String
)
