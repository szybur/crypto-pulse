const reloadButton = document.getElementById("reload-button");
const statusText = document.getElementById("status-text");
const assetsTable = document.getElementById("assets-table");
const assetsTableBody = document.getElementById("assets-table-body");

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

function renderAssets(assets) {
    assetsTableBody.innerHTML = "";

    for (const asset of assets) {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${asset.name}</td>
            <td>${asset.symbol.toUpperCase()}</td>
            <td>${formatPrice(asset.currentPrice)}</td>
            <td>${formatMarketCap(asset.marketCap)}</td>
            <td>
                <a class="details-link" href="/asset.html?id=${encodeURIComponent(asset.id)}">
                    View details
                </a>
            </td>
        `;

        assetsTableBody.appendChild(row);
    }
}

async function loadAssets() {
    statusText.textContent = "Loading assets...";
    assetsTable.classList.add("hidden");
    assetsTableBody.innerHTML = "";

    try {
        const response = await fetch("/api/assets");

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const assets = await response.json();

        if (!Array.isArray(assets) || assets.length === 0) {
            statusText.textContent = "No assets found.";
            return;
        }

        renderAssets(assets);
        statusText.textContent = `Loaded ${assets.length} assets.`;
        assetsTable.classList.remove("hidden");
    } catch (error) {
        console.error(error);
        statusText.textContent = "Failed to load assets. Please try again.";
    }
}

reloadButton.addEventListener("click", loadAssets);

loadAssets();