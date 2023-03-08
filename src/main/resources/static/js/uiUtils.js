let deleteDialog;
let confirmationDialog;

// Updates the name of the employee's record that is going to be tried to delete,
// also updates the submit route for the controller
// @param name  {string}    Name/Description that represent the entry that wants to be deleted.  "Employee: Name LastName"
// @param path  {string}    Base path of the @RequestMapping for the controller that will handle the delete request.
//                          "/employees" -> "/employees/delete/{id}"
// @param id    {number}    Id of the entry we are trying to delete, it will be added to the end of the path.
//
// Thymeleaf:   <a type="button" clas="btn btn-danger" th:href="|javascript:openDeleteDialog('${model.name}', '/employees/positions', ${model.id});|">Delete</a>
function openDeleteDialog(name, path, id) {
  addDeleteDialogElement();
  document.getElementById('deleteName').textContent = name;
  deleteForm.action = `${path}/delete/${id}`;
  deleteDialog.show();
}

// Checks for the existence of the boostraps.Modal object deleteDialog, if it doesn't
// exists check if the HTML Elements exist, if it doesn't exist adds it to the content and
// finally creates the object required to use the modal.
function addDeleteDialogElement() {
  if (deleteDialog) return;
  if (!document.querySelector('#deleteDialog')) {
    let dialogFragment = document.createRange().createContextualFragment(`
        <div id="deleteDialog" class="modal" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Delete entry</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <p>Do you want to delete <span id="deleteName" class="fw-semibold"></span>?. To continue press delete.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                <form id="deleteForm" method="post" action="#">
                                    <button id="btnDeleteEmployee" type="submit" class="btn btn-danger">Delete</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
        `);
    let container = document.querySelector('main.content');
    if (!container) container = document.body; // not using layout
    try {
      container.appendChild(dialogFragment);
    } catch (exception) {
      document.alert(
        'An error has occurred while trying to create the modal Dialog.'
      );
    }
  }
  deleteDialog = new bootstrap.Modal('#deleteDialog', {});
}

// generic confirmation dialog
function openConfirmationDialog(message, actionHref) {
  addConfirmationDialog();
  document.getElementById('genericConfirmationMessage').innerHTML = message;
  document.getElementById('btnGenericConfirmationAccept').onclick = () => {
    confirmationDialog.hide();
    document.location = actionHref;
  };
  confirmationDialog.show();
}

function addConfirmationDialog() {
  if (confirmationDialog) return;
  if (!document.querySelector('#genericConfirmationDialog')) {
    let dialogFragment = document.createRange().createContextualFragment(`
        <div class="modal" id="genericConfirmationDialog" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content">
                    <div class="modal-body">
                        <span id="genericConfirmationMessage">
                            messagee?
                        </span>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-sm  btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button id="btnGenericConfirmationAccept" class="btn btn-sm btn-primary">Continue</button>
                    </div>
                </div>
            </div>
        </div>
        `);
    addDialogToDocument(dialogFragment);
  }
  confirmationDialog = new bootstrap.Modal('#genericConfirmationDialog', {});
}

function addDialogToDocument(dialog) {
  let container = document.querySelector('main.content');
  if (!container) container = document.body; // not using layout
  try {
    container.appendChild(dialog);
  } catch (exception) {
    document.alert(
      'An error has occurred while trying to create the modal Dialog.'
    );
  }
}

// photo upload preview
function displayPhotoPreview(input, imgId) {
  if (input.files.length <= 0) return; // no photo
  let image = document.getElementById(imgId);
  image.src = URL.createObjectURL(input.files[0]);
  image.classList.add('d-block');
}

// table filtering byAll or just Active
function tableFilterShowAll() {
  return {
    btnShowAll: {
      text: 'Show all records',
      icon: 'bi-list',
      event: function () {
        document.location.href = '?showAll=true';
      },
      attributes: {
        title: 'Show all the records active or inactive.',
      },
    },
  };
}

function tableFilterShowActive() {
  return {
    btnShowAll: {
      text: 'Show active records',
      icon: 'bi-filter',
      event: function () {
        document.location.href = '?showAll=false';
      },
      attributes: {
        title: 'Show only active records.',
      },
    },
  };
}

$(function () {
  $('form').on('keypress', function (event) {
    let keyPressed = event.keyCode || event.which;
    if (keyPressed === 13) {
      event.preventDefault();
      return false;
    }
  });
});
