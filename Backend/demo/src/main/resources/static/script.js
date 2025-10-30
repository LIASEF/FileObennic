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
        const response = await fetch("http://localhost:8080/upload", {
            method: "POST",
            body: formData
        });

        if (response.ok) {// Если запрос успешен то
            statusDiv.textContent = "Загрузка завершена!";
            loadFiles(); // Обновляем список файлов после загрузки
        } else {
            statusDiv.textContent = "Ошибка при загрузке файла.";
        }
    } catch (error) {
        statusDiv.textContent = "Произошла ошибка.";//Ошибка файла если что то пошло не так
    }
});

// Функция для загрузки и отображения списка файлов
async function loadFiles() {
    try {
        const response = await fetch("http://localhost:8080/files");
        if (response.ok) {
            const files = await response.json();//Получение  файлов для загрузки
            displayFiles(files);// Вывод файлов
        }
    } catch (error) {
        console.error("Ошибка при загрузке списка файлов:", error);
    }
}

// Функция для отображения файлов в таблице
function displayFiles(files) {
    const container = document.querySelector('.container');// Берем контенйер по селектору
    let table = document.getElementById('files-table');// Cоздаем  переиеннуб для управления будущей табилцей

    if (files.length > 0) {// если файлы есть 
        if (!table) {
            table = document.createElement('table');//Cоздаем таблицу
            table.id = 'files-table';// даем id созданной таблице
            table.innerHTML = '<thead><tr><th>Имя файла</th><th>Ссылка для скачивания</th></tr></thead><tbody></tbody>';// Задаем столбцы
            container.appendChild(table);// добавляем таблицу в контейнер
        }

        const tbody = table.querySelector('tbody');// Находим tbody то есть сами строки
        tbody.innerHTML = ''; // Очищаем предыдущие строки очищаем их при  обновлении

        files.forEach(file => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${file.name}</td>
                <td><a href="${file.downloadUrl}" download="${file.name}">Скачать</a></td>
            `; // Вот здесь добавлен апостраф к ссылки и имени файла чтобы бразуер воспринимал ссылку как загрузочную
            tbody.appendChild(row);
        });
    } else {
        if (table) {
            table.remove();// Если файлов нет удалить
        }
    }
}

// Загружаем файлы при загрузке страницы
document.addEventListener('DOMContentLoaded', loadFiles);
