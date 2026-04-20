const statusText = document.getElementById("status-text");
const assetsTable = document.getElementById("assets-table");
const assetsTableBody = document.getElementById("assets-table-body");

const watchlistStatusText = document.getElementById("watchlist-status-text");
const watchlistTable = document.getElementById("watchlist-table");
const watchlistTableBody = document.getElementById("watchlist-table-body");

const refreshButton = document.getElementById("refresh-button");
const syncStatusText = document.getElementById("sync-status-text");
const syncLastRunText = document.getElementById("sync-last-run-text");

let currentAssets = [];
let currentWatchlist = [];

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

function formatDate(value) {
    if (!value) {
        return "N/A";
    }

    return new Intl.DateTimeFormat("en-US", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(new Date(value));
}

function isOnWatchlist(assetId) {
    return currentWatchlist.some(item => item.assetId === assetId);
}

async function fetchAssets() {
    const response = await fetch("/api/assets");

    if (!response.ok) {
        throw new Error(`Failed to load assets. HTTP ${response.status}`);
    }

    return response.json();
}

async function fetchWatchlist() {
    const response = await fetch("/api/watchlist");

    if (!response.ok) {
        throw new Error(`Failed to load watchlist. HTTP ${response.status}`);
    }

    return response.json();
}

async function addToWatchlist(asset) {
    const response = await fetch("/api/watchlist", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            assetId: asset.id,
            symbol: asset.symbol,
            displayName: asset.name
        })
    });

    if (!response.ok) {
        const errorText = await safeReadError(response);
        throw new Error(errorText || `Failed to add asset. HTTP ${response.status}`);
    }
}

async function removeFromWatchlist(assetId) {
    const response = await fetch(`/api/watchlist/${encodeURIComponent(assetId)}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        const errorText = await safeReadError(response);
        throw new Error(errorText || `Failed to remove asset. HTTP ${response.status}`);
    }
}

async function safeReadError(response) {
    try {
        const data = await response.json();
        return data.error || data.message || null;
    } catch {
        return null;
    }
}

function renderAssets(assets) {
    assetsTableBody.innerHTML = "";

    for (const asset of assets) {
        const row = document.createElement("tr");
        const watched = isOnWatchlist(asset.id);

        row.innerHTML = `
            <td>${asset.name}</td>
            <td>${asset.symbol.toUpperCase()}</td>
            <td>${formatPrice(asset.currentPrice)}</td>
            <td>${formatMarketCap(asset.marketCap)}</td>
            <td>
                <button
                    class="watchlist-button ${watched ? "remove-button" : "add-button"}"
                    data-action="${watched ? "remove" : "add"}"
                    data-asset-id="${asset.id}"
                    data-asset-symbol="${asset.symbol}"
                    data-asset-name="${asset.name}"
                >
                    ${watched ? "Remove" : "Add to watchlist"}
                </button>
            </td>
            <td>
                <a class="details-link" href="/asset.html?id=${encodeURIComponent(asset.id)}">
                    View details
                </a>
            </td>
        `;

        assetsTableBody.appendChild(row);
    }
}

function renderWatchlist(watchlist) {
    watchlistTableBody.innerHTML = "";

    if (!Array.isArray(watchlist) || watchlist.length === 0) {
        watchlistStatusText.textContent = "Watchlist is empty.";
        watchlistTable.classList.add("hidden");
        return;
    }

    for (const item of watchlist) {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.displayName}</td>
            <td>${item.symbol.toUpperCase()}</td>
            <td>${formatDate(item.addedAt)}</td>
            <td>
                <button
                    class="watchlist-button remove-button"
                    data-action="remove"
                    data-asset-id="${item.assetId}"
                >
                    Remove
                </button>
            </td>
        `;

        watchlistTableBody.appendChild(row);
    }

    watchlistStatusText.textContent = `Watchlist contains ${watchlist.length} item(s).`;
    watchlistTable.classList.remove("hidden");
}

async function fetchSyncStatus() {
    const response = await fetch("/api/status/sync");

    if (!response.ok) {
        throw new Error(`Failed to load sync status. HTTP ${response.status}`);
    }

    return response.json();
}

function renderSyncStatus(status) {
    if (!status) {
        syncStatusText.textContent = "Sync status unavailable.";
        syncLastRunText.textContent = "Last refresh: unknown";
        refreshButton.disabled = false;
        refreshButton.textContent = "Refresh assets";
        return;
    }

    syncStatusText.textContent = `Status: ${status.message || "Idle"}`;

    if (status.lastRunAt) {
        syncLastRunText.textContent = `Last refresh: ${formatDate(status.lastRunAt)}`;
    } else {
        syncLastRunText.textContent = "Last refresh: never";
    }

    if (status.running) {
        refreshButton.disabled = true;
        refreshButton.textContent = "Refreshing...";
    } else {
        refreshButton.disabled = false;
        refreshButton.textContent = "Refresh assets";
    }
}

async function triggerRefresh() {
    const response = await fetch("/api/refresh", {
        method: "POST"
    });

    if (!response.ok) {
        const errorText = await safeReadError(response);
        throw new Error(errorText || `Failed to refresh assets. HTTP ${response.status}`);
    }

    return response.json();
}

async function loadDashboard() {
    statusText.textContent = "Loading assets...";
    watchlistStatusText.textContent = "Loading watchlist...";

    assetsTable.classList.add("hidden");
    watchlistTable.classList.add("hidden");

    assetsTableBody.innerHTML = "";
    watchlistTableBody.innerHTML = "";

    try {
        const [assets, watchlist, syncStatus] = await Promise.all([
            fetchAssets(),
            fetchWatchlist(),
            fetchSyncStatus()
        ]);

        currentAssets = assets;
        currentWatchlist = watchlist;

        renderSyncStatus(syncStatus);

        if (!Array.isArray(assets) || assets.length === 0) {
            statusText.textContent = "No assets found.";
        } else {
            renderAssets(assets);
            statusText.textContent = `Loaded ${assets.length} assets.`;
            assetsTable.classList.remove("hidden");
        }

        renderWatchlist(watchlist);
    } catch (error) {
        console.error(error);
        statusText.textContent = error.message || "Failed to load dashboard.";
        watchlistStatusText.textContent = "Failed to load watchlist.";
    }
}

assetsTableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");

    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const assetId = button.dataset.assetId;

    try {
        button.disabled = true;

        if (action === "add") {
            const asset = currentAssets.find(item => item.id === assetId);

            if (!asset) {
                throw new Error("Asset not found in the current dashboard state.");
            }

            await addToWatchlist(asset);
        }

        if (action === "remove") {
            await removeFromWatchlist(assetId);
        }

        await loadDashboard();
    } catch (error) {
        console.error(error);
        alert(error.message || "Operation failed.");
    } finally {
        button.disabled = false;
    }
});

watchlistTableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action='remove']");

    if (!button) {
        return;
    }

    const assetId = button.dataset.assetId;

    try {
        button.disabled = true;
        await removeFromWatchlist(assetId);
        await loadDashboard();
    } catch (error) {
        console.error(error);
        alert(error.message || "Operation failed.");
    } finally {
        button.disabled = false;
    }
});

refreshButton.addEventListener("click", async () => {
    try {
        refreshButton.disabled = true;
        refreshButton.textContent = "Refreshing...";
        syncStatusText.textContent = "Status: Refreshing assets...";

        await triggerRefresh();
        await loadDashboard();
    } catch (error) {
        console.error(error);
        syncStatusText.textContent = `Status: ${error.message || "Refresh failed."}`;
        refreshButton.disabled = false;
        refreshButton.textContent = "Refresh assets";
    }
});

loadDashboard();