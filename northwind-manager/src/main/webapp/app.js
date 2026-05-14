const API_URL = '/api/categories'; 

window.onload = function() {
    loadCategories();
    
    document.getElementById('categoryForm').addEventListener('submit', handleFormSubmit);
};


// get per popolare la tabella
function loadCategories() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', API_URL, true);
    
    xhr.onload = function() {
        if (xhr.status === 200) {
            const categories = JSON.parse(xhr.responseText);
            const tbody = document.getElementById('tableBody');
            tbody.innerHTML = '';
            
            categories.forEach(cat => {
                const tr = document.createElement('tr');
                
                tr.innerHTML = `
                    <td>${cat.id}</td>
                    <td>${cat.name}</td>
                    <td>${cat.description}</td>
                    <td>
                        <button onclick="editCategory(${cat.id}, '${cat.name.replace(/'/g, "\\'")}', '${cat.description.replace(/'/g, "\\'")}')">Modifica</button>
                        <button onclick="deleteCategory(${cat.id})">Elimina</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            console.error('Errore nel caricamento delle categorie.');
        }
    };
    
    xhr.send();
}

//post: se c'è l'id modifica se no inserisce 
function handleFormSubmit(event) {
    event.preventDefault();
    
    const id = document.getElementById('categoryId').value;
    const name = document.getElementById('categoryName').value;
    const description = document.getElementById('description').value;
    
    // crea l'oggetto da inviare
    const categoryData = {
        name: name,
        description: description
    };
    
    const xhr = new XMLHttpRequest();
    
    // se c'è un ID è una modifica (PUT) altrimenti è un inserimento (POST)
    if (id) {
        categoryData.id = parseInt(id);
        xhr.open('PUT', API_URL, true);
    } else {
        xhr.open('POST', API_URL, true);
    }
    
    xhr.setRequestHeader('Content-Type', 'application/json');
    
    xhr.onload = function() {
        if (xhr.status === 200 || xhr.status === 201) {
            resetForm();
            loadCategories(); //ricarica tabella
        } else {
            alert('Errore durante il salvataggio.');
        }
    };
    
    xhr.send(JSON.stringify(categoryData));
}

//3 update
function editCategory(id, name, description) {
    document.getElementById('categoryId').value = id;
    document.getElementById('categoryName').value = name;
    document.getElementById('description').value = description;
    
    document.getElementById('submitBtn').textContent = 'Aggiorna Categoria';
}

//4 delete
function deleteCategory(id) {
    if (confirm('Sei sicuro di voler eliminare questo record?')) {
        const xhr = new XMLHttpRequest();
        xhr.open('DELETE', API_URL + '?id=' + id, true);
        
        xhr.onload = function() {
            if (xhr.status === 200 || xhr.status === 204) {
                loadCategories(); // Aggiorna la vista dopo l'eliminazione
            } else {
                alert('Errore durante l\'eliminazione.');
            }
        };
        
        xhr.send();
    }
}

//reset del form
function resetForm() {
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryId').value = '';
    document.getElementById('submitBtn').textContent = 'Salva';
}