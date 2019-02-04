package br.eng.rodrigoamaro.espressopresentation

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import java.util.concurrent.CountDownLatch

/**
 * 8 - ROBOLECTRIC
 *
 * Esta biblioteca proprõe utilizarmos um simulador Android ao invés de um Emulador ou aparelho
 * físico. Ela oferece um ambiente simulado onde podemos executar testes locais diretamente na JVM
 * com possibilidade de uso de métodos do próprio Android.
 *
 * Em sua versão mais recente, após a incorporação da equipe de desenvolvimento da biblioteca ao
 * Google, a proposta da biblioteca ficou ainda mais próxima do que é feito hoje nos testes
 * instrumentados.
 *
 * Podemos configurar a versão de Android que queremos simular além de características do hardware.
 *
 * Contudo existe um custo em termos de tempo que vem na forma de warm up. Apesar de os testes em si
 * serem extremamente mais rápidos que o testes instrumentados, a penalização de tempo ocorre quando
 * precisamos baixar uma imagem para a versão do sistema operacional configurada. Apesar deste
 * processo ocorrer apenas uma vez para cada versão o tempo de "subida" deste ambiente também possuí
 * um alto custo e, neste caso, ele sempre ocorre no inicio da rotina de testes.
 *
 * Importante notar também que apesar de permitir o uso do Espresso, o fato de termos um ambiente
 * simulado significa que algumas verificações podem não funcionar adequadamente (bugs em aberto)
 * e que não será possível visualizar ou tirar screenshots da tela (ao menos por enquanto)
 *
 * Ainda assim o ganho que pode ser alcando com seu uso faz com que seja uma poderosa ferramenta na
 * mão da equipe.
 */


@RunWith(AndroidJUnit4::class)
class RobolectricTest {

    private val api: Api = mockk()

    @Before
    fun setUp() {
        StandAloneContext.getKoin().loadModules(listOf(module {
            single<Api>(override = true) { api }
        }))
    }

    @Test
    fun successTest() {
        every { api.reactiveCall(any()) } returns Observable.just(true)
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            onView(withId(R.id.edit_username)).perform(typeText("Cebolinha"))
            onView(withId(R.id.button_verify_rx)).perform(click())
            onView(withText("Nome disponível")).check(exists())
        }
        scenario.close()
    }

    @Test
    fun failureTest() {
        every { api.reactiveCall(any()) } returns Observable.just(false)
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            onView(withId(R.id.edit_username)).perform(typeText("Cebolinha"))
            onView(withId(R.id.button_verify_rx)).perform(click())
            onView(withText("Nome indisponível")).check(exists())
        }
        scenario.close()
    }

    private fun exists() = matches(VisibilityMatcher(View.VISIBLE))

    private class VisibilityMatcher(private val visibility: Int) : BaseMatcher<View>() {

        override fun describeTo(description: Description) {
            val visibilityName: String = when (visibility) {
                View.GONE -> "GONE"
                View.VISIBLE -> "VISIBLE"
                else -> "INVISIBLE"
            }
            description.appendText("View visibility must has equals $visibilityName")
        }

        override fun matches(o: Any?): Boolean {
            if (o == null) {
                if (visibility == View.GONE || visibility == View.INVISIBLE)
                    return true
                else if (visibility == View.VISIBLE) return false
            }

            if (o !is View)
                throw IllegalArgumentException("Object must be instance of View. Object is instance of " + o!!)
            return o.visibility == visibility
        }
    }
}

