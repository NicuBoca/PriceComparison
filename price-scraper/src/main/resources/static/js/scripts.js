function enableSpinner() {
    document.getElementById("spinner").style.visibility = "visible";
}

function enableSpinnerNavbar() {
    document.getElementById("spinner-navbar").style.visibility = "visible";
}

function toggleHistory() {
    let history = document.getElementById("history");
    if(history.style.display === "none") {
        history.style.display = "block";
    } else {
        history.style.display = "none";
    }
}