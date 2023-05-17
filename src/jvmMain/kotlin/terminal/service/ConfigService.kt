package terminal.service

class ConfigService(
    override var maxRows: Int,
    override var maxColumns: Int,
    override var title: String
) : IConfigService