document.getElementById("upload-form").addEventListener("submit", async function(event) {//Берем форму по id
    event.preventDefault();// Говорим нет перезагрузке 

    const fileInput = document.getElementById("file-input");//Берем через file input буть файлв
    const file = fileInput.files[0];//Берес сам файл

    if (!file) {
        alert("Выберите файл для загрузки.");
        return;
    }

    const formData = new FormData();//Создаем объект который будет отправлять на сервер
    formData.append("file", file);//Добавляем в этот объект "file" назвается файл file это сам файл

    const statusDiv = document.getElementById("status");
    statusDiv.textContent = "Загрузка..."; // Получаем status меняем на загрузку

    try {
        const response = await fetch("http://localhost:3000/upload", {
            method: "POST",
            body: formData
        });// Отправляем на будущий сервер этот файл

        if (response.ok) {// Если запрос успешен то
            const result = await response.json();// Получаем ссылку на скачивание
            const downloadLink = document.getElementById("download-link");// Переменаая для обращения ваша ссылка для скачиваеия
            const linkElement = document.getElementById("link");//Обращение к самой ссылке

            linkElement.href = result.downloadUrl;// даем ссылку на скачивание
            downloadLink.style.display = "block";//ПОказываем ссылку
            statusDiv.textContent = "Загрузка завершена!";//Меняем статус загрузка завершена
        } else {
            statusDiv.textContent = "Ошибка при загрузке файла.";
        }
    } catch (error) {
        statusDiv.textContent = "Произошла ошибка.";//Ошибка файла если что то пошло не так
    }
});
