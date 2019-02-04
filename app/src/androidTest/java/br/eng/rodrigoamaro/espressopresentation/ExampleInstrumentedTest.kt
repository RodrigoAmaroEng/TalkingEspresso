package br.eng.rodrigoamaro.espressopresentation

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isChecked
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withParent
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.Runnable
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.getKoin
import kotlin.coroutines.CoroutineContext

/***************************************************************************************************
 *
 *  █████╗ ██╗   ██╗████████╗ ██████╗ ███╗   ███╗ █████╗  ██████╗ █████╗  ██████╗
 * ██╔══██╗██║   ██║╚══██╔══╝██╔═══██╗████╗ ████║██╔══██╗██╔════╝██╔══██╗██╔═══██╗
 * ███████║██║   ██║   ██║   ██║   ██║██╔████╔██║███████║██║     ███████║██║   ██║
 * ██╔══██║██║   ██║   ██║   ██║   ██║██║╚██╔╝██║██╔══██║██║     ██╔══██║██║   ██║
 * ██║  ██║╚██████╔╝   ██║   ╚██████╔╝██║ ╚═╝ ██║██║  ██║╚██████╗██║  ██║╚██████╔╝
 * ╚═╝  ╚═╝ ╚═════╝    ╚═╝    ╚═════╝ ╚═╝     ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝ ╚═════╝
 *
 * ██████╗ ███████╗    ████████╗███████╗███████╗████████╗███████╗███████╗
 * ██╔══██╗██╔════╝    ╚══██╔══╝██╔════╝██╔════╝╚══██╔══╝██╔════╝██╔════╝
 * ██║  ██║█████╗         ██║   █████╗  ███████╗   ██║   █████╗  ███████╗
 * ██║  ██║██╔══╝         ██║   ██╔══╝  ╚════██║   ██║   ██╔══╝  ╚════██║
 * ██████╔╝███████╗       ██║   ███████╗███████║   ██║   ███████╗███████║
 * ╚═════╝ ╚══════╝       ╚═╝   ╚══════╝╚══════╝   ╚═╝   ╚══════╝╚══════╝
 *
 *  ██████╗ ██████╗ ███╗   ███╗    ███████╗███████╗██████╗ ██████╗ ███████╗███████╗███████╗ ██████╗
 * ██╔════╝██╔═══██╗████╗ ████║    ██╔════╝██╔════╝██╔══██╗██╔══██╗██╔════╝██╔════╝██╔════╝██╔═══██╗
 * ██║     ██║   ██║██╔████╔██║    █████╗  ███████╗██████╔╝██████╔╝█████╗  ███████╗███████╗██║   ██║
 * ██║     ██║   ██║██║╚██╔╝██║    ██╔══╝  ╚════██║██╔═══╝ ██╔══██╗██╔══╝  ╚════██║╚════██║██║   ██║
 * ╚██████╗╚██████╔╝██║ ╚═╝ ██║    ███████╗███████║██║     ██║  ██║███████╗███████║███████║╚██████╔╝
 * ╚═════╝ ╚═════╝ ╚═╝     ╚═╝    ╚══════╝╚══════╝╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝ ╚═════╝

 *
 * 1 - A PIRÂMIDE DE TESTES
 *
 *          / \             OUTROS
 *         /___\
 *        /     \           ACEITAÇÃO
 *       /_______\
 *      /         \         INTERFACE
 *     /___________\
 *    /             \       INTEGRAÇÂO
 *   /_______________\
 *  /                 \     UNITÁRIOS
 * /___________________\
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * 2 - ESPRESSO e UI AUTOMATOR
 *
 * - Ferramentas do Google para automação de testes
 * - ESPRESSO = Testes INTERNOS em uma aplicação
 * - UI AUTOMATOR = Testes no nível de SO
 * - Aplicados em testes de aceitação e/ou interface
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * 3 - SETUP BÁSICO DO ESPRESSO
 *
 */
@RunWith(AndroidJUnit4::class)
class BasicSetupTestClass {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun singleTest() {
    }
}

/**
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * 4 - ESTRUTURA DE UM TESTE
 */
@RunWith(AndroidJUnit4::class)
class StructureTestClass {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setUp() {
        // Pode ser usado para configurar contextos que são utilizados por todos os testes dessa classe
    }

    @After
    fun tearDown() {
        // Pode ser usado para liberar um recurso ou limpar um estado ao final do teste
    }

    @Test
    fun singleTest() {
        // Dado que... (Algum contexto prévio, configuração ou ajuste para colocar a aplicação em um cenário específico)

        // Quando eu... (Uma ou mais ações necessárias para que se verifique o comportamento da aplicação)

        // Então... (Verificação de que a aplicação se comportou como esperado)
    }
}
/**
 * 5 - PRIMEIROS PASSOS COM O ESPRESSO
 */
@RunWith(AndroidJUnit4::class)
class FirstTestClass {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun singleTest() {
        // Dado que...
        // Quando eu preencher o campo Username...
        onView(withId(R.id.edit_username)).perform(typeText("Cebolinha"))
        // E pressionar o botão verificar...
        onView(withId(R.id.button_verify)).perform(click())
        // Então a aplicação deve consultar o nome informado
        onView(withText("Consultando...")).check(matches(isDisplayed()))
    }
}

/**
 * As instruções do Espresso são divididas em 2 partes: Matcher e Action/Assertion
 *
 * Quando estamos realizando uma ação temos o par Matcher+Action
 * Quanto estamos realizando uma verificação temos o par Matcher+Assertion
 *
 * Para iniciar a instrução Espresso fazendo uma validação ou ação sobre um View utilizamos o método
 * "onView" passando como argumento um Matcher
 *
 * MATCHERS
 *
 * Ao chamar o método onView estamos dizendo sobre qual view vamos efetuar a operação representada
 * pela classe ViewInteraction. Para tanto, temos a disposição uma gama de Matchers disponibilizados
 * pela biblioteca Hamcrest (inclusa no Espresso):
 *
 * - withId: View com o ID especificado
 * - withText: View com o Texto especificado (Aceita também um String Resource)
 * - withHint: View com o Texto Hint especificado (Aceita também um String Resource)
 * - withParent: View que contém uma View (Os atributos dessa view também podem ser especificados)
 * como "Pai" na estrutura XML
 * - instanceOf: View cujo tipo é igual a classe especificada
 * - isDescendantOfA: View que é descendente de uma View (Os atributos dessa view também podem ser especificados)
 * na estrutura XML
 * - isDisplayed: Verifica se a View tem ao menos 90% de sua área visível
 * - isEnabled: Verifica se a View está habilitada
 * - isChecked: Verifica se a View está checada (Ex: Radio ou Check box)
 *
 * Entre outros
 *
 * Também podemos utilizar agregadores e operadores para modificar e agrupar Matchers
 *
 * - allOf(Matcher1, Matcher2...MatcherN): Todos os Matchers especificados
 * - anyOf(Matcher1, Matcher2...MatcherN): Ao menos um dos Matchers especificados
 * - not(Matcher): Para verificar se a View não se adequa ao Matchers especificado
 *
 * Entre outros
 *
 * ACTIONS
 *
 * Actions permitem interagir com os componentes presentes na tela simulando uma ação efetuada pelo
 * usuário. Ao usar uma Action chamamos o método "perform" da classe ViewInteraction passando uma ou
 * mais Actions separadas por vírgula como argumento. Assim como nos Matchers, temos uma gama de
 * ações disponibilizadas pela biblioteca e que também podem ser customizadas
 *
 * - click/longClick/doubleClick: Efetua a operação de click na view especificada pelo Matcher
 * - typeText/replaceText/clearText: Insere, altera e limpa o texto de uma caixa de texto
 * - scrollTo: Rola a tela até o objeto específicado. Só pode ser utilizado se o objeto estiver
 * dentro de uma view que suporte scroll
 * - swipe[Direction]: Efetua a operação de swipe na direção especificada [Down,Right,Left,Up]
 * - pressMenuKey: Pressiona a tecla de menu do Android. Ainda que não presente no dispositivo ela
 * ativa o menu dropdown da aplicação caso exista um
 * - pressBack: Pressiona o botão voltar
 * - pressKey(KeyCode): Envia um comando de pressionamento da tecla especificada na ação
 *
 * ASSERTIONS
 *
 * Assertions permitem verificar se uma View se encontra em determinado estado. Ao usar um Assertion
 * chamamos o método "check" da classe ViewInteraction. Este método deve receber uma Assertion como
 * argumento. Assertions não definem a checagem em si, mas globam um ou mais Matchers que podem ser
 * usados para realizar a validação. Os matchers mais importantes disponíveis pela plataforma são:
 *
 * - matches(Matcher): Valida se a Vew se adequa ao Matcher informado
 * - doesNotExist: Valida se a View não está presente na árvore de componentes da tela
 *
 * CONTROLES ESPECIAIS
 *
 * onData e ListViews
 *
 * Para verificar informações e executar ações sobre views contidas em ListViews temos o método
 * "onData". De maneira similar ao "onView" ele permite passar um ou mais Matchers para fazer a
 * busca em um componente do tipo ListView.
 *
 * RecyclerViews
 *
 * Quando nossas listas são implementadas com RecyclerViews o melhor a ser fazer é adicionar a
 * biblioteca "espresso-contrib". Ela fornece uma série de métodos auxiliares que permitem facilitar
 * a interação e validação:
 *
 * - actionOnItemAtPosition(position, action): Efetua a ação no item da posição especificada
 * - actionOnItem(matcher, action): Efetua uma ação no item que atende os critérios especificados
 * - scrollTo(matcher): Rola a lista até a primeira View que atende os critérios especificados
 * - scrollToPosition(position): Rola a lista até a View na posição especificada
 *
 * De maneira similar a biblioteca oferece mecanismos personalizados para os seguintes controles:
 *
 * - Drawer
 * - BottomNavigation
 * - Pickers
 * - View Pager
 *
 * Mais sobre o assunto em:
 * https://developer.android.com/training/testing/espresso/
 *
 */
@RunWith(AndroidJUnit4::class)
class ListingTestClass {

    @get:Rule
    val rule = ActivityTestRule<ListingActivity>(ListingActivity::class.java)

    @Test
    fun singleTest() {
        // Essa view não está presente na tela pois ainda não foi criada pelo RecyclerView
        onView(withText("Guatemala")).check(doesNotExist())

        // Essa view está presente e visível na tela...
        onView(withText("Finlândia")).check(matches(isDisplayed()))

        // ... porém, não está completamente visível
        onView(withText("Finlândia")).check(matches(not(isDisplayingAtLeast(50))))

        // Garante que a view com o texto especificado é filha do RecyclerView
        onView(allOf(withParent(withId(R.id.listing)), withText("Brasil")))
            .check(matches(isDisplayed()))

        // Fazemo o scroll até a view até a view desejada. Perceba que o
        // método auxiliar do RecyclerView permite que executemos o scroll mesmo que a view não
        // exista ainda
        onView(withId(R.id.listing)).perform(
            RecyclerViewActions.actionOnItem<SimpleHolder>(
                withText("Guatemala"), scrollTo()
            )
        )

        // E agora a view está presente e visível
        onView(withText("Guatemala")).check(matches(isDisplayed()))

        // Realizando click na view específica
        onView(withId(R.id.listing)).perform(
            RecyclerViewActions.actionOnItem<SimpleHolder>(
                withText("Guatemala"), click()
            )
        )

        // Após o click podemos verificar se a View encontra-se marcada
        onView(withId(R.id.listing)).check(
            matches(hasDescendant(allOf(withText("Guatemala"), isChecked())))
        )
    }
}

/**
 *
 *
 *
 *
 *
 *
 * 6 - CHAMADAS DE REDE E OUTRAS OPERAÇÕES DE LONGA DURAÇÃO
 *
 * O Espresso conta com um mecanismo interno que aguarda a execução da tarefa anterior para
 * prosseguir com a próxima instrução. Isso impede que nossos testes quebrem dependendo do disposito
 * em que estão rodando (Ex. Aparelhos com menor capacidade de processamento). Contudo, algumas
 * estruturas comuns no código que escrevemos não são avaliadas por este mecanismo. Quando isso
 * ocorre, é necessário dizer ao Espresso que temos uma operação sendo executada para que aguarde o
 * fim da mesma (que também deve ser informado por nós).
 *
 * A interface que nos permite fazer isso chama-se IdlingResource e é utilizada pelo Espresso para
 * verificar recursos em uso que impede o processamento da próxima instrução. A biblioteca fornece
 * uma implementação básica desta interface para pequenos controles chamada CountingIdlingResource
 *
 * Além disso, algumas bibliotecas nos ajudam a evitar estes problemas em estruturas amplamente
 * usadas como é o caso do RxIdler
 *
 **/
@RunWith(AndroidJUnit4::class)
class ServerCallTestClass {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java, true, false)

    @Before
    fun setUp() {
        // Registrando o IdlingResource para Rx
        RxJavaPlugins.setInitNewThreadSchedulerHandler(
            Rx2Idler.create("RxJava 2.x Computation Scheduler")
        )
    }

    @Test
    fun reactiveLongRunningTest() {
        rule.launchActivity(Intent())
        // Quando eu preencher o campo Username...
        onView(withId(R.id.edit_username)).perform(typeText("Cebolinha"))
        // E pressionar o botão verificar...
        onView(withId(R.id.button_verify_rx)).perform(click())
        // Então a aplicação deve mostrar o resultado após a consulta
        onView(withText("Nome disponível")).check(matches(isDisplayed()))
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    @Test
    fun coRoutineLongRunningTest() {
        // Registrando o IdlingResource para CoRoutines
        val idlingResource = RoutineIdlingResource()
        IdlingRegistry.getInstance().register(idlingResource.handler)

        // Injetando um novo CoroutineDispatcher com ligação ao IdlingResource
        getKoin().loadModules(listOf(module {
            single<CoroutineDispatcher>(override = true) { idlingResource }
        }))

        rule.launchActivity(Intent())
        // Dado que...
        // Quando eu preencher o campo Username...
        onView(withId(R.id.edit_username)).perform(typeText("Cebolinha"))
        // E pressionar o botão verificar...
        onView(withId(R.id.button_verify)).perform(click())
        // Então a aplicação deve consultar o nome informado
        onView(withText("Nome disponível")).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource.handler)
    }
}

// Class para garantir que o Espresso espere o final da execução da CoRoutine
@UseExperimental(InternalCoroutinesApi::class)
class RoutineIdlingResource :
    CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.increment()
        block.run()
        handler.decrement()
    }

    val handler = CountingIdlingResource("Coroutine Idling Resource")
}

/**
 *
 *
 * 7 - ROBOT PATTERN
 *
 * Visando dar maior legibilidade e capacidade de reutilização de código aos testes, alguns padrões
 * já conhecidos de outras plataformas podem ser utilizados nos testes com Espresso. O Robot Pattern
 * é um desses padrões e se propõe a oferecer métodos que encapsulam uma ação do usuário ou uma
 * verificação de estado expondo uma interface que descreva claramente a operação.
 *
 * Vejamos então como o teste anterior poderia ser reescrito utilizando esse padrão
 *
 **/
@RunWith(AndroidJUnit4::class)
class RobotTestClass {

    class Automator(private val rule: ActivityTestRule<MainActivity>) {
        fun dadoQueServicoNaoAceitaOUsuario(): Automator {
            getKoin().loadModules(listOf(module {
                single<Api>(override = true) { FailApi() }
            }))
            return this
        }

        fun iniciar(): Robot {
            rule.launchActivity(Intent())
            return Robot()
        }
    }

    class Robot {

        fun preencherNomeDoUsuario(nome: String): Robot {
            onView(withId(R.id.edit_username)).perform(typeText(nome))
            return this
        }

        fun pressionarBotaoVerificar(): Robot {
            onView(withId(R.id.button_verify_rx)).perform(click())
            return this
        }

        fun validarSe() = Validator()
    }

    class Validator {
        fun nomeEstaDisponivel(): Validator {
            onView(withText("Nome disponível")).check(matches(isDisplayed()))
            return this
        }

        fun nomeNaoEstaDisponivel(): Validator {
            onView(withText("Nome indisponível")).check(matches(isDisplayed()))
            return this
        }
    }

    /** AQUI PODERÍAMOS USAR UMA BIBLIOTECA DE MOCK **/
    class FailApi : Api {
        override suspend fun coroutineCall(someArgument: String): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun reactiveCall(someArgument: String): Observable<Boolean> =
            Observable.just(false)
    }

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java, true, false)

    @Before
    fun setUp() {
        // Registrando o IdlingResource para Rx
        RxJavaPlugins.setInitNewThreadSchedulerHandler(
            Rx2Idler.create("RxJava 2.x Computation Scheduler")
        )
    }

    val automacao = Automator(rule)

    /**
     * Ao rodar esta classe de teste verificamos que dependendo da ordem em que os testes são
     * executados um dos testes pode falhar. Isso ocorre porque o contexto do primeiro teste está
     * contaminando o teste seguinte. No caso específico desse exemplo o problema ocorre pois quando
     * realizamos a injeção de depêndencia na criação da aplição, não existe nenhum código setando o
     * contexto para o teste "robotSuccessTest". Dado que, NORMALMENTE, a aplicação não é reiniciada
     * entre testes, o objeto injetado no teste "robotFailureTest" permanece em uso nos testes
     * seguintes. Para evitar este problema podemos usar o Test Orchestrator, que nos garante testes
     * herméticamente isolados evitando a contaminação do contexto
     */
    @Test
    fun robotSuccessTest() {
        automacao
            .iniciar()
            .preencherNomeDoUsuario("Cebolinha")
            .pressionarBotaoVerificar()
            .validarSe()
            .nomeEstaDisponivel()
    }

    @Test
    fun robotFailureTest() {
        automacao
            .dadoQueServicoNaoAceitaOUsuario()
            .iniciar()
            .preencherNomeDoUsuario("Cebolinha")
            .pressionarBotaoVerificar()
            .validarSe()
            .nomeNaoEstaDisponivel()
    }
}
/**
 * É interessante notar que o uso do desse padrão para escrita de testes trás maiores benefícios
 * quando temos o compartilhamento de etapas entre os testes, como ocorre por exemplo com o método
 * "pressionarBotaoVerificar"
 **/