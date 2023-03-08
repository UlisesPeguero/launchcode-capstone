

function uiCalendar(events){
    var calendarEl=document.getElementById('calendar');


    var calendar = new FullCalendar.Calendar(calendarEl, {
        timeZone: 'UTC',
        themeSystem:'bootstrap5',
        initialView: 'dayGridWeek',
        headerToolbar: {
          left: 'prev,next',
          center: 'title',
          right: 'dayGridWeek,dayGridDay'
        },
        events:events
      });


    calendar.render();
}