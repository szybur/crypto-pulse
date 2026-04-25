package com.pulse.crypto.clients

data class BinanceAssetMapping(
    val assetId: String,
    val assetSymbol: String
)

fun topMarketSymbolsLowercase(): List<String> = listOf(
    "btcusdt",
    "ethusdt",
    "bnbusdt",
    "solusdt",
    "xrpusdt",
    "adausdt",
    "dogeusdt",
    "trxusdt",
    "avaxusdt",
    "dotusdt"
)

object BinanceAssetMappings {
    private val mappings = mapOf(
        "BTCUSDT" to BinanceAssetMapping(
            assetId = "bitcoin",
            assetSymbol = "btc"
        ),
        "ETHUSDT" to BinanceAssetMapping(
            assetId = "ethereum",
            assetSymbol = "eth"
        ),
        "BNBUSDT" to BinanceAssetMapping(
            assetId = "binancecoin",
            assetSymbol = "bnb"
        ),
        "SOLUSDT" to BinanceAssetMapping(
            assetId = "solana",
            assetSymbol = "sol"
        ),
        "XRPUSDT" to BinanceAssetMapping(
            assetId = "ripple",
            assetSymbol = "xrp"
        ),
        "ADAUSDT" to BinanceAssetMapping(
            assetId = "cardano",
            assetSymbol = "ada"
        ),
        "DOGEUSDT" to BinanceAssetMapping(
            assetId = "dogecoin",
            assetSymbol = "doge"
        ),
        "TRXUSDT" to BinanceAssetMapping(
            assetId = "tron",
            assetSymbol = "trx"
        ),
        "AVAXUSDT" to BinanceAssetMapping(
            assetId = "avalanche-2",
            assetSymbol = "avax"
        ),
        "DOTUSDT" to BinanceAssetMapping(
            assetId = "polkadot",
            assetSymbol = "dot"
        ),
        "LINKUSDT" to BinanceAssetMapping(
            assetId = "chainlink",
            assetSymbol = "link"
        ),
        "MATICUSDT" to BinanceAssetMapping(
            assetId = "matic-network",
            assetSymbol = "matic"
        ),
        "LTCUSDT" to BinanceAssetMapping(
            assetId = "litecoin",
            assetSymbol = "ltc"
        ),
        "BCHUSDT" to BinanceAssetMapping(
            assetId = "bitcoin-cash",
            assetSymbol = "bch"
        ),
        "ATOMUSDT" to BinanceAssetMapping(
            assetId = "cosmos",
            assetSymbol = "atom"
        ),
        "ETCUSDT" to BinanceAssetMapping(
            assetId = "ethereum-classic",
            assetSymbol = "etc"
        ),
        "XLMUSDT" to BinanceAssetMapping(
            assetId = "stellar",
            assetSymbol = "xlm"
        ),
        "NEARUSDT" to BinanceAssetMapping(
            assetId = "near",
            assetSymbol = "near"
        ),
        "UNIUSDT" to BinanceAssetMapping(
            assetId = "uniswap",
            assetSymbol = "uni"
        ),
        "APTUSDT" to BinanceAssetMapping(
            assetId = "aptos",
            assetSymbol = "apt"
        ),
        "FILUSDT" to BinanceAssetMapping(
            assetId = "filecoin",
            assetSymbol = "fil"
        ),
        "ARBUSDT" to BinanceAssetMapping(
            assetId = "arbitrum",
            assetSymbol = "arb"
        ),
        "OPUSDT" to BinanceAssetMapping(
            assetId = "optimism",
            assetSymbol = "op"
        ),
        "SUIUSDT" to BinanceAssetMapping(
            assetId = "sui",
            assetSymbol = "sui"
        ),
        "INJUSDT" to BinanceAssetMapping(
            assetId = "injective-protocol",
            assetSymbol = "inj"
        ),
        "AAVEUSDT" to BinanceAssetMapping(
            assetId = "aave",
            assetSymbol = "aave"
        ),
        "HBARUSDT" to BinanceAssetMapping(
            assetId = "hedera-hashgraph",
            assetSymbol = "hbar"
        ),
        "VETUSDT" to BinanceAssetMapping(
            assetId = "vechain",
            assetSymbol = "vet"
        ),
        "ICPUSDT" to BinanceAssetMapping(
            assetId = "internet-computer",
            assetSymbol = "icp"
        ),
        "ALGOUSDT" to BinanceAssetMapping(
            assetId = "algorand",
            assetSymbol = "algo"
        )
    )

    fun get(marketSymbol: String): BinanceAssetMapping? =
        mappings[marketSymbol.uppercase()]

    fun supportedMarketSymbols(): Set<String> = mappings.keys

    fun supportedMarketSymbolsLowercase(): List<String> =
        mappings.keys.map { it.lowercase() }
}
