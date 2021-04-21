const editButtons = document.getElementsByClassName("edit-button")
const deleteButtons = document.getElementsByClassName("delete-button")
const exportButton = document.getElementById("export-button")

for (const element of editButtons) {
    let id = element.id.replace('edit', '')
    element.addEventListener('click', () => { window.location=`edit/${id}` })
}

for (const element of deleteButtons) {
    let id = element.id.replace('delete', '')
    element.addEventListener('click', () => {
        fetch(`api/person/delete/${id}`, {
        method: 'POST'
        }).then(response => element.parentElement.parentElement.outerHTML = '')
    })
}