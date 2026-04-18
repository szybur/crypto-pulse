const detailsStatusText = document.getElementById("details-status-text");
const detailsContent = document.getElementById("details-content");

const assetImage = document.getElementById("asset-image");
const assetName = document.getElementById("asset-name");
const assetSymbol = document.getElementById("asset-symbol");
const assetPrice = document.getElementById("asset-price");
const assetMarketCap = document.getElementById("asset-market-cap");
const assetRank = document.getElementById("asset-rank");
const assetChange24h = document.getElementById("asset-change-24h");
const assetDescription = document.getElementById("asset-description");
const assetHomepage = document.getElementById("asset-homepage");

const historyStatusText = document.getElementById("history-status-text");
const historyList = document.getElementById("history-list");

function getAssetIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

function formatPrice(value) {
    if (value === null || value === undefined) {
        return "N/A";
    }

    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 2
    }).format(value);
}

function formatMarketCap(value) {
    if (value === null || value === undefined) {
        return "N/A";
    }

    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 0
    }).format(value);
}

function formatPercentage(value) {
    if (value === null || value === undefined) {
        return "N/A";
    }

    return `${value.toFixed(2)}%`;
}

function formatDate(timestamp) {
    const date = new Date(timestamp);

    return new Intl.DateTimeFormat("en-US", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(date);
}

function renderAssetDetails(asset) {
    assetName.textContent = asset.name ?? "Unknown asset";
    assetSymbol.textContent = asset.symbol ? asset.symbol.toUpperCase() : "N/A";
    assetPrice.textContent = formatPrice(asset.currentPrice);
    assetMarketCap.textContent = formatMarketCap(asset.marketCap);
    assetRank.textContent = asset.marketCapRank ?? "N/A";
    assetChange24h.textContent = formatPercentage(asset.priceChangePercentage24h);
    assetDescription.textContent = asset.description || "No description available.";

    if (asset.homepage) {
        assetHomepage.textContent = asset.homepage;
        assetHomepage.href = asset.homepage;
    } else {
        assetHomepage.textContent = "Not available";
        assetHomepage.removeAttribute("href");
    }

    if (asset.imageUrl) {
        assetImage.src = asset.imageUrl;
        assetImage.classList.remove("hidden");
    } else {
        assetImage.classList.add("hidden");
    }
}

function renderHistory(history) {
    historyList.innerHTML = "";

    if (!Array.isArray(history) || history.length === 0) {
        historyStatusText.textContent = "No price history available.";
        historyList.classList.add("hidden");
        return;
    }

    for (const point of history) {
        const item = document.createElement("li");
        item.textContent = `${formatDate(point.timestamp)} — ${formatPrice(point.price)}`;
        historyList.appendChild(item);
    }

    historyStatusText.textContent = `Loaded ${history.length} history points.`;
    historyList.classList.remove("hidden");
}

async function loadAssetDetails(assetId) {
    const response = await fetch(`/api/assets/${encodeURIComponent(assetId)}`);

    if (!response.ok) {
        if (response.status === 404) {
            throw new Error("Asset not found.");
        }
        throw new Error(`Failed to load asset details. HTTP ${response.status}`);
    }

    return response.json();
}

async function loadAssetHistory(assetId) {
    const response = await fetch(`/api/assets/${encodeURIComponent(assetId)}/history?days=7`);

    if (!response.ok) {
        if (response.status === 404) {
            throw new Error("Price history not found.");
        }
        throw new Error(`Failed to load asset history. HTTP ${response.status}`);
    }

    return response.json();
}

async function initializePage() {
    const assetId = getAssetIdFromUrl();

    if (!assetId) {
        detailsStatusText.textContent = "Missing asset id in the URL.";
        historyStatusText.textContent = "History could not be loaded.";
        return;
    }

    try {
        const asset = await loadAssetDetails(assetId);
        renderAssetDetails(asset);

        detailsStatusText.textContent = "";
        detailsContent.classList.remove("hidden");
    } catch (error) {
        console.error(error);
        detailsStatusText.textContent = error.message || "Failed to load asset details.";
        historyStatusText.textContent = "History could not be loaded.";
        return;
    }

    try {
        const history = await loadAssetHistory(assetId);
        renderHistory(history);
    } catch (error) {
        console.error(error);
        historyStatusText.textContent = error.message || "Failed to load price history.";
    }
}

initializePage();