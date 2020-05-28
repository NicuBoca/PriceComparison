function enableSpinner() {
    document.getElementById("spinner").style.visibility = "visible";
}

function enableSpinnerNavbar() {
    document.getElementById("spinnerNavbar").style.visibility = "visible";
}

function checkInput() {
    if(document.getElementById("searchInput").value==="") {
        document.getElementById('submitBtn').disabled = true;
    } else {
        document.getElementById('submitBtn').disabled = false;
    }
}

function toggleHistory() {
    let history = document.getElementById("history");
    if(history.style.display === "none") {
        history.style.display = "block";
    } else {
        history.style.display = "none";
    }
}