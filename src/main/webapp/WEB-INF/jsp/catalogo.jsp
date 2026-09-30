<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="https://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<body>
    <h1>Catalogo Katane</h1>

    <c:if test="${param.successo eq 'true' }">
        <p style="color: green;">Ordine completato con successo!</p>
    <c/:if>

    <table border="1">
        <tr>
            <th>Nome</th>
            <th>Acciaio</th>
            <th>Prezzo</th>
            <th>Email</th>
            <th>Disponibiltà</th>
            <th>Azione</th>
        </tr>
        <c:forEach var="k" items="${elencoKatane}">
            <tr>
                <tf><c:out value="${k.nome}" /></td>
                <th><c:out value="${k.tipoAcciaio" /></td>
                <th><c:out value="${k.prezzo" /></td>
                <th><c:out value="${k.giacenza_magazzino"/></td>
                <td>
                    <form action="<c:url value='/katane/acquista'/>" method="post">
                        <input type="hidden" name="id" value="${k.id}" />
                        <input type="hidden" name="quantia" value="1" min="1" max="${k.giacenza_magazzino}" />
                        <input typr="hidden" name="acquista" />
                    </form>
                </td>
            <tr>
        </c:forEach>
    </table>
</body>
</html>