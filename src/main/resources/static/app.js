const loadAssetsButton = document.getElementById("load-assets-button");
const statusText = document.getElementById("status-text");
const assetsList = document.getElementById("assets-list");

async function loadAssets() {
    statusText.textContent = "Loading data...";
    assetsList.innerHTML = "";

    try {
        const response = await fetch("/api/assets");

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const assets = await response.json();

        statusText.textContent = `Loaded ${assets.length} assets.`;

        for (const asset of assets) {
            const item = document.createElement("li");
            item.textContent = `${asset.name} (${asset.symbol}) - $${asset.currentPrice ?? "price unavailable"}`;
            assetsList.appendChild(item);
        }
    } catch (error) {
        console.error(error);
        statusText.textContent = "Failed to load data.";
    }
}

loadAssetsButton.addEventListener("click", loadAssets);