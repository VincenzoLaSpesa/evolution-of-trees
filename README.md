##evolution-of-trees

Evoluzione genetica applicata agli alberi di classificazione, la mia tesi di laurea

### Stato del progetto

Il progetto è in fase di reingegnerizzazione, attualmente è (direttamente) impossibile la compilazione non essendo incluse informazioni sulle librerie necessarie.
Completata la reingegnerizzazione, e in particolare rimosse le dipendenze dal framework Weka provvedendo a riscrivere J48, verrà prodotta la documentazione e incluse
le informazioni sulla compilazione e il deploy.

### Mappa dei sorgenti

    .
    ├── matlab
script vari per eseguire test con matlab, non strettamente legati al progetto

    ├── README.md
    ├── script

script ruby per eseguire dei benchmark sul progetto.

	└── tesi

codice java del progetto

	├── controllers

package dei controllers, classi che operano sui dati implementando le operazioni
	
	├── interfaces

package delle interfacce, classi che presentano i dati in altri formati
	
	├── launcher

package dei lanciatori, classi che implementano l'interfaccia runnable e permettono di eseguire gli algoritmi
	
	├── main

package main	
	
	├── models

package dei modelli, classi che implementano le strutture dati
	
	
	│   ├── popolazione

	package dei modelli relativi alla popolazione

	└── util

package util
	
	
	    ├── logging

package dei delle classi che implementano il logging
	    