package com.example.biktrak.ui.routes

import java.util.ArrayList
import java.util.HashMap

object Route{

    val route: MutableList<Item> = ArrayList()
    val route_map: MutableMap<String, Item> = HashMap()
    init {
        addItem(Item("1", "Morasko", "Moraska Góra ma 153,8 m n.p.m. i jest to najwyższe wzniesienie na terenie Poznania. Jest to więc propozycja dla tych, którzy chcieliby pobiegać po górach. Jest to świetne miejsce do ćwiczenia podbiegów, z jednej strony góry podbieg ma 70 m. Góra Moraska znajduje się na terenie rezerwatu przyrody „Meteoryt Morasko”. Można zrobić pętlę, startując z osiedla Batorego, jest tam znakowany żółty szlak turystyczny, który kończy się na osiedlu Bolesława Śmiałego. Cała trasa to około 12 km."))
        addItem(Item("2", "Wzdłuż Warty", "Tereny nad Wartą to dobre miejsce dla tych, którzy lubią biegi przełajowe, teren jest urozmaicony. Są również odcinki asfaltowe, wszystko zależy od tego, w którym miejscu będziemy biegać. Trzeba jednak pamiętać, że okresowo tereny nad Wartą mogą być niedostępne z powodu wylewów rzeki."))
        addItem(Item("3", "Pole Mokotowskie", "środek miasta i bardzo popularna trasa. Pole Mokotowskie to nie tylko miejsce spotkan warszawiaków, ale i teren gzie można przeprowadzić pełny trenign biegowy. I to zarówno jeśli jesteście początkujący, jak i bardziej zaawansowani."))
        addItem(Item("4", "Lasek Marceliński", "W Lasku Marcelińskim jest sporo ścieżek do biegania. W jednym miejscu jest niewielkie wzniesienie. Są także wytyczone dwie trasy biegowe, jedna o długości 4,12 km a druga 2,45."))
        addItem(Item("5", "Jezioro Malta", "To najpopularniejsze miejsce wśród Poznaniaków nie tylko do biegania, ale do uprawiania rekreacji w ogóle. Asfaltowa pętla dookoła jeziora Malta to 5,4 km, odcinki trasy są oznaczone. Można wydłużyć trasę i pobiec w kierunku ulicy Browarnej. Tam już pobiegniemy po drogach leśnych w przyjemnej okolicy. Można zrobić pętlę i wrócić nad Maltę. Nad Maltą organizowanych jest sporo zawodów biegowych, najczęściej na 5 albo na 10 km."))
    }

    private fun addItem(item: Item) {
        route.add(item)
        route_map.put(item.id, item)
    }

    data class Item(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}