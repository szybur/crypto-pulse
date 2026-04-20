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
const chartWrapper = document.getElementById("chart-wrapper");
const historyChart = document.getElementById("history-chart");

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

function formatShortDate(timestamp) {
    const date = new Date(timestamp);

    return new Intl.DateTimeFormat("en-US", {
        month: "short",
        day: "numeric"
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

function renderHistoryChart(history) {
    historyChart.innerHTML = "";

    if (!Array.isArray(history) || history.length === 0) {
        historyStatusText.textContent = "No price history available.";
        chartWrapper.classList.add("hidden");
        return;
    }

    const width = 700;
    const height = 260;
    const padding = 40;

    const prices = history.map(point => point.price);
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);

    const safeMinPrice = minPrice === maxPrice ? minPrice - 1 : minPrice;
    const safeMaxPrice = minPrice === maxPrice ? maxPrice + 1 : maxPrice;

    function x(index) {
        if (history.length === 1) {
            return width / 2;
        }

        return padding + (index / (history.length - 1)) * (width - padding * 2);
    }

    function y(price) {
        const normalized = (price - safeMinPrice) / (safeMaxPrice - safeMinPrice);
        return height - padding - normalized * (height - padding * 2);
    }

    const gridTop = padding;
    const gridBottom = height - padding;
    const gridLeft = padding;
    const gridRight = width - padding;

    const topLine = document.createElementNS("http://www.w3.org/2000/svg", "line");
    topLine.setAttribute("x1", gridLeft);
    topLine.setAttribute("y1", gridTop);
    topLine.setAttribute("x2", gridRight);
    topLine.setAttribute("y2", gridTop);
    topLine.setAttribute("class", "chart-grid-line");
    historyChart.appendChild(topLine);

    const middleLine = document.createElementNS("http://www.w3.org/2000/svg", "line");
    middleLine.setAttribute("x1", gridLeft);
    middleLine.setAttribute("y1", (gridTop + gridBottom) / 2);
    middleLine.setAttribute("x2", gridRight);
    middleLine.setAttribute("y2", (gridTop + gridBottom) / 2);
    middleLine.setAttribute("class", "chart-grid-line");
    historyChart.appendChild(middleLine);

    const bottomLine = document.createElementNS("http://www.w3.org/2000/svg", "line");
    bottomLine.setAttribute("x1", gridLeft);
    bottomLine.setAttribute("y1", gridBottom);
    bottomLine.setAttribute("x2", gridRight);
    bottomLine.setAttribute("y2", gridBottom);
    bottomLine.setAttribute("class", "chart-grid-line");
    historyChart.appendChild(bottomLine);

    const axisY = document.createElementNS("http://www.w3.org/2000/svg", "line");
    axisY.setAttribute("x1", gridLeft);
    axisY.setAttribute("y1", gridTop);
    axisY.setAttribute("x2", gridLeft);
    axisY.setAttribute("y2", gridBottom);
    axisY.setAttribute("class", "chart-axis");
    historyChart.appendChild(axisY);

    const axisX = document.createElementNS("http://www.w3.org/2000/svg", "line");
    axisX.setAttribute("x1", gridLeft);
    axisX.setAttribute("y1", gridBottom);
    axisX.setAttribute("x2", gridRight);
    axisX.setAttribute("y2", gridBottom);
    axisX.setAttribute("class", "chart-axis");
    historyChart.appendChild(axisX);

    const pathData = history
        .map((point, index) => `${index === 0 ? "M" : "L"} ${x(index)} ${y(point.price)}`)
        .join(" ");

    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", pathData);
    path.setAttribute("class", "chart-line");
    historyChart.appendChild(path);

    const pointStep = Math.max(1, Math.ceil(history.length / 8));

    for (let i = 0; i < history.length; i += pointStep) {
        const point = history[i];

        const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute("cx", x(i));
        circle.setAttribute("cy", y(point.price));
        circle.setAttribute("r", 3);
        circle.setAttribute("class", "chart-point");
        historyChart.appendChild(circle);
    }

    const lastPoint = history[history.length - 1];
    const lastCircle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    lastCircle.setAttribute("cx", x(history.length - 1));
    lastCircle.setAttribute("cy", y(lastPoint.price));
    lastCircle.setAttribute("r", 3);
    lastCircle.setAttribute("class", "chart-point");
    historyChart.appendChild(lastCircle);

    const minLabel = document.createElementNS("http://www.w3.org/2000/svg", "text");
    minLabel.setAttribute("x", 8);
    minLabel.setAttribute("y", gridBottom + 4);
    minLabel.setAttribute("class", "chart-label");
    minLabel.textContent = formatPrice(safeMinPrice);
    historyChart.appendChild(minLabel);

    const midLabel = document.createElementNS("http://www.w3.org/2000/svg", "text");
    midLabel.setAttribute("x", 8);
    midLabel.setAttribute("y", ((gridTop + gridBottom) / 2) + 4);
    midLabel.setAttribute("class", "chart-label");
    midLabel.textContent = formatPrice((safeMinPrice + safeMaxPrice) / 2);
    historyChart.appendChild(midLabel);

    const maxLabel = document.createElementNS("http://www.w3.org/2000/svg", "text");
    maxLabel.setAttribute("x", 8);
    maxLabel.setAttribute("y", gridTop + 4);
    maxLabel.setAttribute("class", "chart-label");
    maxLabel.textContent = formatPrice(safeMaxPrice);
    historyChart.appendChild(maxLabel);

    const startDate = document.createElementNS("http://www.w3.org/2000/svg", "text");
    startDate.setAttribute("x", gridLeft);
    startDate.setAttribute("y", height - 8);
    startDate.setAttribute("class", "chart-label");
    startDate.textContent = formatShortDate(history[0].timestamp);
    historyChart.appendChild(startDate);

    const endDate = document.createElementNS("http://www.w3.org/2000/svg", "text");
    endDate.setAttribute("x", gridRight - 55);
    endDate.setAttribute("y", height - 8);
    endDate.setAttribute("class", "chart-label");
    endDate.textContent = formatShortDate(history[history.length - 1].timestamp);
    historyChart.appendChild(endDate);

    chartWrapper.classList.remove("hidden");
}

function renderHistory(history) {
    if (!Array.isArray(history) || history.length === 0) {
        historyStatusText.textContent = "No price history available.";
        chartWrapper.classList.add("hidden");
        return;
    }

    historyStatusText.textContent = `Loaded ${history.length} history points.`;
    renderHistoryChart(history);
}

async function loadAssetScreenData(assetId) {
    const response = await fetch(`/api/assets/${encodeURIComponent(assetId)}/screen?days=7`);

    if (!response.ok) {
        if (response.status === 404) {
            throw new Error("Asset not found.");
        }
        throw new Error(`Failed to load asset screen data. HTTP ${response.status}`);
    }

    return response.json();
}

async function initializePage() {
    const assetId = getAssetIdFromUrl();

    if (!assetId) {
        detailsStatusText.textContent = "Missing asset id in the URL.";
        historyStatusText.textContent = "History could not be loaded.";
        chartWrapper.classList.add("hidden");
        return;
    }

    try {
        const screenData = await loadAssetScreenData(assetId);

        renderAssetDetails(screenData.details);
        renderHistory(screenData.history);

        detailsStatusText.textContent = "";
        detailsContent.classList.remove("hidden");
    } catch (error) {
        console.error(error);
        detailsStatusText.textContent = error.message || "Failed to load asset screen data.";
        historyStatusText.textContent = "History could not be loaded.";
        chartWrapper.classList.add("hidden");
    }
}

initializePage();
