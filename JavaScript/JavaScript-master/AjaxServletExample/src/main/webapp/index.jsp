<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <script type="text/javascript">

        function getXmlHttp(){
            var xmlhttp;
            try {
                xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (E) {
                    xmlhttp = false;
                }
            }
            if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
                xmlhttp = new XMLHttpRequest();
            }
            return xmlhttp;
        }


        function getCities(select) {

            var city = select.options[select.selectedIndex].value;
            var xmlhttp = getXmlHttp();
            xmlhttp.open('GET', '/ajax-test-1.0-SNAPSHOT/cities?country=' + city, false);
            xmlhttp.send(null);
            if(xmlhttp.status == 200) {
                var cities = xmlhttp.responseText.split('|');
                var options = '';
                for (var i=0; i < cities.length; i++){
                    options += '<option>' + cities[i] + '</option>';
                }
                document.getElementById('sitySelect').innerHTML = options;
            }

        }


    </script>


<h3> Select country</h3>
<select onchange="getCities(this)">
    <c:forEach items="${countries}" var="c">
        <option value="${c}">${c}</option>
    </c:forEach>
</select>

<h3>Cities</h3>

<select id="sitySelect"></select>