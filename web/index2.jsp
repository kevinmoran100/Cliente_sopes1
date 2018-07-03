<%-- 
    Document   : index
    Created on : 30-jun-2018, 8:35:18
    Author     : kevin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SOPES1</title>
    </head>
    <body>
        <H1>Equivalencia de Monedas</H1>
        Conozca la tasa de cambio de las diferentes monedas del mundo.Ingrese la cantidad de dinero a convertir en la primera ventana.Luego elija las monedas de cuales desea conocer la equivalencia.
        <form action="ServletRedis" method="post">
            <fieldset>
                <input type="hidden" name="enviar" value="si">
                <br><br>
                <input type="submit" value="Redis"/>
            </fieldset>
        </form>
        <form action="ServletCassandra" method="post">
            <fieldset>
                <input type="hidden" name="enviar" value="si">
                <br><br>
                <input type="submit" value="Cassandra"/>
            </fieldset>
        </form>
    </body>
</html>