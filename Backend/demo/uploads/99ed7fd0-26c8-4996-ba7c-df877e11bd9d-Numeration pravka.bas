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
    
    ' ������� ������ ���������� ��������� ��� ������ ����� ����� "�������"
    Set regex = CreateObject("VBScript.RegExp")
    regex.IgnoreCase = True
    regex.Global = True
    regex.pattern = "�������\s*(\d+)" ' ���� "�������" � ����� ����� ����
    
    ' �������� ������� ��������
    Set doc = ActiveDocument
    
    ' ��������� ���������
    num = 1
    
    ' �������� �� ������� ���������
    For Each paragraph In doc.Paragraphs
        text = paragraph.Range.text
        
        ' ���������, ���� �� � ������ "�������" � ������
        Set match = regex.Execute(text)
        
        If match.Count > 0 Then
            ' ������� �����, �������� ������ �����
            cleanText = match(0).Submatches(0)
            
            ' ����������� ������ � �����
            If IsNumeric(cleanText) Then
                currentNumber = CInt(cleanText)
                
                ' ���� ����� �� ��������� � ���������, �������� ���
                If currentNumber <> num Then
                    ' �������� ������ �����, �������� ����� "�������"
                    paragraph.Range.text = "������� " & num
                End If
            End If
        Else
            ' ���� � ������ ��� "�������", ��������� ���������� �����
            paragraph.Range.text = "������� " & num
        End If
        
        ' ����������� ����� ��� ���������� "�������"
        num = num + 1
    Next paragraph
End Sub

