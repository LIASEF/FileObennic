Attribute VB_Name = "NewMacros"
Sub Macros()
    Dim doc As Document
    Dim paragraph As paragraph
    Dim text As String
    Dim num As Integer
    Dim cleanText As String
    Dim regex As Object
    Dim match As Object
    Dim currentNumber As Integer
    
    ' Создаем объект регулярных выражений для поиска числа после "Рисунок"
    Set regex = CreateObject("VBScript.RegExp")
    regex.IgnoreCase = True
    regex.Global = True
    regex.pattern = "Рисунок\s*(\d+)" ' Ищем "Рисунок" и числа после него
    
    ' Получаем текущий документ
    Set doc = ActiveDocument
    
    ' Начальная нумерация
    num = 1
    
    ' Проходим по каждому параграфу
    For Each paragraph In doc.Paragraphs
        text = paragraph.Range.text
        
        ' Проверяем, есть ли в тексте "Рисунок" с числом
        Set match = regex.Execute(text)
        
        If match.Count > 0 Then
            ' Очищаем текст, извлекая только число
            cleanText = match(0).Submatches(0)
            
            ' Преобразуем строку в число
            If IsNumeric(cleanText) Then
                currentNumber = CInt(cleanText)
                
                ' Если номер не совпадает с ожидаемым, заменяем его
                If currentNumber <> num Then
                    ' Заменяем только номер, оставляя текст "Рисунок"
                    paragraph.Range.text = "Рисунок " & num
                End If
            End If
        Else
            ' Если в тексте нет "Рисунок", добавляем правильный номер
            paragraph.Range.text = "Рисунок " & num
        End If
        
        ' Увеличиваем номер для следующего "Рисунка"
        num = num + 1
    Next paragraph
End Sub

