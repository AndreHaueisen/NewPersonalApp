package com.andrehaueisen.fitx.register

import com.andrehaueisen.fitx.Utils
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.FormElement

/**
 * Created by andre on 1/21/2017.
 */
class CrefValidator {

    private val BASE_URL = "http://www.confef.org.br/extra/registrados/"

    fun isPersonalValid(personalName: String, stateCode: Int, cref: String): Boolean {

        val formattedPersonalName = Utils.formatPersonalName(personalName) // without spelling accent and uppercased
        val formattedCref = Utils.formatCrefNumber(cref) //add missing zeros


        val response = Jsoup.connect(BASE_URL).method(Connection.Method.GET).execute()

        val document = response.parse()
        val form = document.getElementById("pesquisa") as FormElement

        form.getElementById("login3").attr("value", formattedPersonalName)
        form.getElementsByTag("select")[0].getElementsByTag("option")[stateCode + 1].attr("selected", "selected") //set the option state as selected

        val responseDoc = form.submit().post()

        val elementsFromColumn = responseDoc.getElementById("content2column").getElementsByTag("p")

        val responseCref : CharSequence

        if (elementsFromColumn != null && !elementsFromColumn.isEmpty()) {
            responseCref = elementsFromColumn[0].getElementsByTag("i").text().subSequence(5, 11)
        } else {
            return false
        }
        return responseCref.equals(formattedCref)

    }

    private fun getStateCode(state: String): Int {

        when (state) {
            "AC" -> return 1
            "AL" -> return 2
            "AM" -> return 3
            "AP" -> return 4
            "BA" -> return 5
            "CE" -> return 6
            "DF" -> return 7
            "ES" -> return 8
            "GO" -> return 9
            "MA" -> return 10
            "MT" -> return 11
            "MS" -> return 12
            "MG" -> return 13
            "PA" -> return 14
            "PB" -> return 15
            "PR" -> return 16
            "PE" -> return 17
            "PI" -> return 18
            "RJ" -> return 19
            "RN" -> return 20
            "RS" -> return 21
            "RR" -> return 22
            "RO" -> return 23
            "SC" -> return 24
            "SP" -> return 25
            "SE" -> return 26
            "TO" -> return 27
            else -> return 0
        }
    }
}