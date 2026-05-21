const API_URL = '/api/suppliers';

window.onload = function () {
    loadSuppliers();
    document.getElementById('supplierForm').addEventListener('submit', handleFormSubmit);
};

// ── GET: carica tutti i fornitori nella tabella ──────────────────────────────
function loadSuppliers() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', API_URL + '/', true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            const suppliers = JSON.parse(xhr.responseText);
            const tbody = document.getElementById('tableBody');
            tbody.innerHTML = '';

            if (suppliers.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#999;">Nessun fornitore trovato.</td></tr>';
                return;
            }

            suppliers.forEach(s => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${s.supplierID}</td>
                    <td>${esc(s.companyName)}</td>
                    <td>${esc(s.contactName)}</td>
                    <td>${esc(s.contactTitle)}</td>
                    <td>${esc(s.city)}</td>
                    <td>${esc(s.country)}</td>
                    <td>${esc(s.phone)}</td>
                    <td>
                        <button class="btn-edit"   onclick="editSupplier(${s.supplierID})">✏️ Modifica</button>
                        <button class="btn-delete" onclick="deleteSupplier(${s.supplierID})">🗑️ Elimina</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            setStatus('Errore nel caricamento dei fornitori (HTTP ' + xhr.status + ')');
        }
    };

    xhr.onerror = function () { setStatus('Errore di rete.'); };
    xhr.send();
}

// ── POST / PUT ───────────────────────────────────────────────────────────────
function handleFormSubmit(event) {
    event.preventDefault();
    setStatus('');

    const id = document.getElementById('supplierId').value;

    const data = {
        companyName:  document.getElementById('companyName').value.trim(),
        contactName:  document.getElementById('contactName').value.trim(),
        contactTitle: document.getElementById('contactTitle').value.trim(),
        address:      document.getElementById('address').value.trim(),
        city:         document.getElementById('city').value.trim(),
        region:       document.getElementById('region').value.trim(),
        postalCode:   document.getElementById('postalCode').value.trim(),
        country:      document.getElementById('country').value.trim(),
        phone:        document.getElementById('phone').value.trim(),
        fax:          document.getElementById('fax').value.trim(),
        homePage:     document.getElementById('homePage').value.trim()
    };

    if (!data.companyName) {
        setStatus('Il campo "Ragione Sociale" è obbligatorio.');
        return;
    }

    const xhr = new XMLHttpRequest();

    if (id) {
        // PUT /api/suppliers/{id}
        xhr.open('PUT', API_URL + '/' + id, true);
    } else {
        // POST /api/suppliers/
        xhr.open('POST', API_URL + '/', true);
    }

    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onload = function () {
        if (xhr.status === 200 || xhr.status === 201) {
            resetForm();
            loadSuppliers();
        } else {
            try {
                const err = JSON.parse(xhr.responseText);
                setStatus('Errore: ' + err.message);
            } catch (e) {
                setStatus('Errore HTTP ' + xhr.status);
            }
        }
    };

    xhr.onerror = function () { setStatus('Errore di rete.'); };
    xhr.send(JSON.stringify(data));
}

// ── Carica dati nel form per la modifica ─────────────────────────────────────
function editSupplier(id) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', API_URL + '/' + id, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            const s = JSON.parse(xhr.responseText);
            document.getElementById('supplierId').value    = s.supplierID;
            document.getElementById('companyName').value   = s.companyName   || '';
            document.getElementById('contactName').value   = s.contactName   || '';
            document.getElementById('contactTitle').value  = s.contactTitle  || '';
            document.getElementById('address').value       = s.address       || '';
            document.getElementById('city').value          = s.city          || '';
            document.getElementById('region').value        = s.region        || '';
            document.getElementById('postalCode').value    = s.postalCode    || '';
            document.getElementById('country').value       = s.country       || '';
            document.getElementById('phone').value         = s.phone         || '';
            document.getElementById('fax').value           = s.fax           || '';
            document.getElementById('homePage').value      = s.homePage      || '';

            document.getElementById('submitBtn').textContent = '💾 Aggiorna';
            window.scrollTo({ top: 0, behavior: 'smooth' });
        } else {
            setStatus('Impossibile caricare il fornitore ' + id);
        }
    };

    xhr.send();
}

// ── DELETE ───────────────────────────────────────────────────────────────────
function deleteSupplier(id) {
    if (!confirm('Eliminare il fornitore con ID ' + id + '?')) return;

    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', API_URL + '/' + id, true);

    xhr.onload = function () {
        if (xhr.status === 200 || xhr.status === 204) {
            loadSuppliers();
        } else {
            try {
                const err = JSON.parse(xhr.responseText);
                setStatus('Errore: ' + err.message);
            } catch (e) {
                setStatus('Errore HTTP ' + xhr.status);
            }
        }
    };

    xhr.onerror = function () { setStatus('Errore di rete.'); };
    xhr.send();
}

// ── Utility ──────────────────────────────────────────────────────────────────
function resetForm() {
    document.getElementById('supplierForm').reset();
    document.getElementById('supplierId').value = '';
    document.getElementById('submitBtn').textContent = '💾 Salva';
    setStatus('');
}

function setStatus(msg) {
    document.getElementById('statusMsg').textContent = msg;
}

// Previene XSS nel render della tabella
function esc(val) {
    if (!val) return '';
    return String(val)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}