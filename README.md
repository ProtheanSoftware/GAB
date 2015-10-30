# gab
An android app for making new friends on the go. For the electricity innovation challenge and as a part of Chalmers course DAT255.

<h2>docs</h2>
Documentation regarding technical choices and reflections of the project.

<h2>Getting started</h2>
git clone https://github.com/ProtheanSoftware/gab.git

Read Developer documentation in docs.

gabDB_init.sql can be run on a MySQL server to create/re-initialize the database.


# App source package structure

<h2>activities package</h2>

FacebookLogin, acivity that logs user into facebook, starts mainacivity

MainActivity, The mainactivity

SplashScreen, Splashscreen which starts facebooklogin

<h2>adapter package</h2>

MatchesListAdapter, The adapter for "confirmed" matches.

MessageAdapter, The adapter for the messagefragment

PagerAdapter, The adapter for the tabs

<h2>fragment package</h2>

LogoutFragment, Fragment for logging user out

MatchPopup, Not implemented as of yet, pops up at match

MatchScreenFragment, Fragment which shows all "potential" matches

MatchesListFragment, Fragment for listing all "confirmed" matches

MessagingFragment, Fragment for chat

<h2>handler package</h2>

BusHandler, Used for polling apis for status of bus.

DataHandler, Holds the main data of the application

IDatabaseHandler, Interface for datasehandler if we want to switch later

JdbcDatabaseHandler, Databasehandler using Jdbc(Java DataBase Connector)

MessageService, Service for polling sinch(Send message/Recieve instantly)

<h2>model package</h2>

Like, data model for holding like data

Profile, data model for holding database users

MatchProfile, extends profile, used for "confirmed" matches; we can see which bus they are on

Message, message datamodel

Session, datamodel for sessions.

# Team
Tobias Alldén
David Ström
Oskar Jedvert
Oscar Boking
Oscar Hall

Built at Chalmers, 2015
