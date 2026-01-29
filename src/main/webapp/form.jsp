<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Formulaire</title>
</head>
<body>
<h1>Saisissez votre nom</h1>
<form method="post" action="hello-servlet">
    <label>Votre nom : </label>
    <input type="text" name="nom" required>
    <button type="submit">Envoyer</button>
</form>
</body>
</html>