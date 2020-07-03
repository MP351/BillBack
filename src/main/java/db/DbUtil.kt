package db

data class SpeedLimit(val downlink: String, val uplink: String) {
    override fun toString(): String {
        return "$downlink/$uplink"
    }
}