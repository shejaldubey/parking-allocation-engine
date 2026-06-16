const SPOTS_PER_FLOOR = 25;
const TOTAL_FLOORS = 5;

const parkForm = document.getElementById('park-form');
const leaveForm = document.getElementById('leave-form');
const refreshBtn = document.getElementById('refresh-btn');
const parkingGrid = document.getElementById('parking-grid');
const messageArea = document.getElementById('message-area');
const startSimBtn = document.getElementById('start-sim-btn');
const stopSimBtn = document.getElementById('stop-sim-btn');

let isSimulating = false;
let parkedDummyPlates = [];

function showMessage(text, type) {
    messageArea.textContent = text;
    messageArea.className = 'message-area visible ' + type;
}

function clearMessage() {
    messageArea.textContent = '';
    messageArea.className = 'message-area';
}

function getFloorForSpot(spotIndex) {
    return Math.floor(spotIndex / SPOTS_PER_FLOOR) + 1;
}

function groupSpotsByFloor(spots) {
    const sortedSpots = [...spots].sort((a, b) => a.id - b.id);
    const floors = {};
    for (let i = 0; i < sortedSpots.length; i++) {
        const floor = getFloorForSpot(i);
        if (!floors[floor]) floors[floor] = [];
        floors[floor].push(sortedSpots[i]);
    }
    return floors;
}

function renderSpotCell(spot) {
    const cell = document.createElement('div');
    const statusClass = spot.occupied ? 'occupied' : 'free';
    
    // This class triggers the colors in style.css (size-SMALL, etc.)
    cell.className = `spot-cell ${statusClass} size-${spot.size}`;
    cell.title = `Spot #${spot.id} | Size: ${spot.size} | Distance: ${spot.distanceToExit}`;

    const idSpan = document.createElement('span');
    idSpan.className = 'spot-id';
    idSpan.textContent = '#' + spot.id;
    cell.appendChild(idSpan);

    const infoSpan = document.createElement('span');
    infoSpan.className = 'spot-plate';
    
    // If occupied show Plate, if Free show Size
    infoSpan.textContent = (spot.occupied && spot.currentLicensePlate) ? spot.currentLicensePlate : spot.size;
    cell.appendChild(infoSpan);
    
    return cell;
}

function renderParkingGrid(spots) {
    parkingGrid.innerHTML = '';
    const floors = groupSpotsByFloor(spots);

    for (let floor = 1; floor <= TOTAL_FLOORS; floor++) {
        const floorSpots = floors[floor] || [];
        const section = document.createElement('section');
        section.className = 'floor-section';
        section.innerHTML = `<h3 style="margin-bottom:10px;">Floor ${floor}</h3>`;

        const grid = document.createElement('div');
        grid.className = 'floor-grid';

        floorSpots.forEach(spot => grid.appendChild(renderSpotCell(spot)));
        section.appendChild(grid);
        parkingGrid.appendChild(section);
    }
}

function updateStats(spots) {
    const total = spots.length;
    const occupied = spots.filter(s => s.occupied).length;
    const free = total - occupied;
    document.getElementById('stat-total').textContent = total;
    document.getElementById('stat-free').textContent = free;
    document.getElementById('stat-occupied').textContent = occupied;
}

async function fetchSpots() {
    const response = await fetch('/api/spots');
    if (!response.ok) throw new Error('Failed to load parking spots');
    return response.json();
}

async function loadAndRenderSpots() {
    try {
        const spots = await fetchSpots();
        renderParkingGrid(spots);
        updateStats(spots);
    } catch (error) {
        parkingGrid.innerHTML = `<p class="loading">Error loading spots: ${error.message}</p>`;
        showMessage('Could not load parking grid.', 'error');
    }
}

async function handleParkSubmit(event) {
    event.preventDefault();
    if(isSimulating) return; 
    const data = { licensePlate: document.getElementById('park-license').value, size: document.getElementById('park-size').value };
    const res = await fetch('/api/park', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
    if (res.ok) { showMessage("Parked!", "success"); parkForm.reset(); await loadAndRenderSpots(); }
    else { const e = await res.json(); showMessage(e.error, 'error'); }
}

async function handleLeaveSubmit(event) {
    event.preventDefault();
    if(isSimulating) return; 
    const data = { licensePlate: document.getElementById('leave-license').value };
    const res = await fetch('/api/leave', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
    if (res.ok) { showMessage("Vehicle Left!", "success"); leaveForm.reset(); await loadAndRenderSpots(); }
    else { const e = await res.json(); showMessage(e.error, 'error'); }
}

async function runSimulation() {
    isSimulating = true;
    startSimBtn.disabled = true; stopSimBtn.disabled = false;
    parkedDummyPlates = [];
    const sizes = ['SMALL', 'MEDIUM', 'LARGE'];
    
    for (let i = 0; i < 65; i++) {
        if (!isSimulating) break;
        const plate = `SIM-${Math.floor(Math.random() * 9000)}`;
        const res = await fetch('/api/park', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({licensePlate: plate, size: sizes[i%3]})});
        if(res.ok) { parkedDummyPlates.push(plate); await loadAndRenderSpots(); await new Promise(r => setTimeout(r, 80)); }
    }
    await new Promise(r => setTimeout(r, 1000));
    for (let i = 0; i < 32; i++) {
        if (!isSimulating || parkedDummyPlates.length === 0) break;
        await fetch('/api/leave', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({licensePlate: parkedDummyPlates.pop()})});
        await loadAndRenderSpots(); await new Promise(r => setTimeout(r, 80));
    }
    isSimulating = false; startSimBtn.disabled = false; stopSimBtn.disabled = true;
    showMessage("Simulation Complete!", "success");
}

parkForm.addEventListener('submit', handleParkSubmit);
leaveForm.addEventListener('submit', handleLeaveSubmit);
startSimBtn.addEventListener('click', runSimulation);
stopSimBtn.addEventListener('click', () => isSimulating = false);
refreshBtn.addEventListener('click', async () => {
    refreshBtn.disabled = true; await loadAndRenderSpots();
    showMessage("Grid refreshed!", "success"); refreshBtn.disabled = false;
});
document.addEventListener('DOMContentLoaded', loadAndRenderSpots);