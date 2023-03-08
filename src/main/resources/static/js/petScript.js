
const dogList = document.getElementById('dogDropList');

//EVENT LISTENERS
// window load
window.addEventListener('load', populateList);

//populate List
function populateList(){
  fetch('https://dog.ceo/api/breeds/list/all')
    .then(response => response.json())
    .then(data => createListItems(data.message))
}

//createListItems
function createListItems(data){
  let output = '';
  Object.keys(data).forEach(key => output+=`<option value="${key}">${(key)}</option>`);
  document.getElementById('dogDropList').innerHTML = output;
}
