# CPS3230 Assignment
Our submission for the CPS3230 Assignment.
These are the specifications of the system:
1. Every time an agent wants to use the system, they must first contact their
supervisor who will decide if it is safe for them to use the system at that point in
time. If it is safe, then the supervisor will give them a special login key consisting
of a 10-character string, which they must use to log into the system within 1
minute or the key will become invalid.
2. Once logged in, the agent can send and receive a maximum of 25 messages, at
which point they will be logged out of the system and have to log in again.
3. Messages can only be a maximum of 140 characters long and should not
contain any blocked words. Blocked words are words which should not be said
between agents and for the scope of this assignment, a list of blocked words can
be hard coded into your system as [“recipe”, “ginger”, “nuclear”]
4. Users of the system will also be logged out after 10 minutes of having logged in,
regardless of whether they have sent/received their quota of messages.
5. When agents send a message to another agent, it is placed in a mailbox, which
the receiver can access. Messages that are in the inbox for more than 30
minutes will be deleted.