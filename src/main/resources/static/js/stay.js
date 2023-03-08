let endDate = {
  input: null,
  value: null,
  disabled: true,
  button: null,
  icons: ['bi-x-circle', 'bi-pen'],
  message: ['Cancel changes to end date', 'Change end date'],
};

function toggleEndDate() {
  let disabled = (endDate.disabled = !endDate.disabled);
  endDate.input.prop('disabled', disabled);
  endDate.button.attr('title', endDate.message[+disabled]);
  endDate.button
    .children('i')
    .attr('class', 'bi')
    .addClass(endDate.icons[+disabled]);
}

let newCount;
let additionalServices;
let additionalServiceDialog;
let confirmRemoveDialog;

// additional services
function addAdditionalService() {
  if (!$('#additionalServicesContainer').is(':visible'))
    $('#additionalServicesContainer').show(200);
  openAdditionalServicesDialog();
}

function openAdditionalServicesDialog(id, index) {
  $('.add-input').val('').trigger('change');
  if (id) {
    $('#btnSaveAdditionalService')
      .attr('onclick', `addOrUpdateAdditionalService(false, ${id}, ${index})`)
      .text('Update');
    let data = additionalServices[id];
    for (let name in data) {
      $('#addServiceForm')
        .find('[name="' + name + '"]')
        .val(data[name])
        .trigger('change');
    }
  } else {
    // new service
    $('#btnSaveAdditionalService')
      .attr('onclick', `addOrUpdateAdditionalService(true)`)
      .html('Add <i class="bi-plus"></i>');
  }
  additionalServiceDialog.show();
}

function addOrUpdateAdditionalService(add, id, index) {
  const tableBody = $('#additionalServicesTable > tbody');
  if (add) {
    index = currentIndex;
    let newId = 'new_' + newCount++;
    let template = $($('#newAdditionalServiceRow')[0].content.cloneNode(true));
    template.find('tr').attr('data-index', index);
    let data = updateServiceInformation(index, template);
    template
      .find('#_btnEdit')
      .click(() => openAdditionalServicesDialog(newId, index));
    template
      .find('#_btnRemove')
      .click(() => removeServicesDialog(newId, index));
    $('.no-records-found').remove();
    additionalServices[newId] = data;
    tableBody.append(template);
    updateTableStripes();
    currentIndex++;
  } else {
    let tr = tableBody.find(`tr[data-index=${index}]`);
    let data = updateServiceInformation(index, tr);
    additionalServices[id] = data;
  }
  additionalServiceDialog.hide();
}

function updateServiceInformation(index, tr) {
  let data = {};
  for (let { name, value } of $('#addServiceForm').serializeArray()) {
    data[name] = value;
    tr.find(`input[data-field=_${name}]`)
      .val(value)
      .attr('id', (i, value) => value.replace('__index__', index))
      .attr('name', (i, value) => value.replace('__index__', index));
    tr.find(`td[data-field=_${name}]`).text(value);
  }
  tr.find('td[data-field=_serviceText]').text(
    $('#addService option:selected').text()
  );
  return data;
}

function openConfirmRemoveDialog(id, index) {
  $('#btnRemoveService').click(() => removeAdditionalService(id, index));
  confirmRemoveDialog.show();
}

function removeAdditionalService(id, index) {
  confirmRemoveDialog.hide();
  let row = $(`tr[data-index=${index}]`);
  row.hide();
  row.find('input[data-field=_serviceId]').val('');
  updateTableStripes();
}

function updateUnitPrice(select) {
  $('#addPricePerUnit')
    .val(select.selectedOptions[0].dataset.priceperunit)
    .trigger('change');
}

function updateSubtotal() {
  const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  });
  let subTotal = $('#addQuantity').val() * $('#addPricePerUnit').val();
  $('#addSubTotal').val(formatter.format(subTotal));
}

function updateTableStripes() {
  $('#additionalServicesTable tr:visible').each(function (index) {
    $(this).css(
      'background-color',
      !!(index & 1) ? 'rgba(0,0,0,.05)' : 'rgba(0,0,0,0)'
    );
  });
}

$(function () {
  endDate.input = $('#endDate');
  endDate.value = endDate.input.val();
  endDate.button = $('#btnEnableEndDate');
  //
  newCount = 0;
  additionalServiceDialog = new bootstrap.Modal('#addServiceDialog', {});
  confirmRemoveDialog = new bootstrap.Modal('#confirmRemoveDialog', {});
  updateTableStripes();
});
