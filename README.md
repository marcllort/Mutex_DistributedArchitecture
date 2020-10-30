# Mutex_DistributedArchitecture

This project is about implementing different mutex algorithms, Lamport Algorithm and Ricart and Agrawala Algorithm. 

This application must have two heavyweight processes, ProcessA and ProcessB. 

ProcessA must invoke 3 lightweight processes ProcessLWA1, ProcessLWA2 and ProcessLWA3.

ProcessB must invoke 2 processes ProcessLWB1, ProcessLWB2.

Each lightweight process must live in a infinite loop that consists of displaying its identifier on the screen for 10 times while waiting 1 second at a time
and time. Both heavyweight processes will run on the same machine, so all lightweight processes will compete for the same resource: the screen. 

- A token-based policy will need to be implemented between the two heavyweight processes. 
- Among the processes invoked by ProcessA, a Lamport policy will have to be implemented for mutual exclusion.
- Among the processes invoked by ProcessB, a Ricart and Agrawala policy will have to be implemented for mutual exclusion.

## Lamport

Lamport’s Distributed Mutual Exclusion Algorithm is a permission based algorithm proposed by Lamport as an illustration of his synchronization scheme for distributed systems.
In permission based timestamp is used to order critical section requests and to resolve any conflict between requests.

In Lamport’s Algorithm critical section requests are executed in the increasing order of timestamps i.e a request with smaller timestamp will be given permission to execute critical section first than a request with larger timestamp.

Three type of messages ( REQUEST, REPLY and RELEASE) are used and communication channels are assumed to follow FIFO order.

- A site send a REQUEST message to all other site to get their permission to enter critical section.
- A site send a REPLY message to requesting site to give its permission to enter the critical section.
- A site send a RELEASE message to all other site upon exiting the critical section.
- Every site Si, keeps a queue to store critical section requests ordered by their timestamps. request_queuei denotes the queue of site Si
- A timestamp is given to each critical section request using Lamport’s logical clock.
- Timestamp is used to determine priority of critical section requests. Smaller timestamp gets high priority over larger timestamp. The execution of critical section request is always in the order of their timestamp.

### To enter Critical section:
- When a site Si wants to enter the critical section, it sends a request message Request(tsi, i) to all other sites and places the request on request_queuei. Here, Tsi denotes the timestamp of Site Si
- When a site Sj receives the request message REQUEST(tsi, i) from site Si, it returns a timestamped REPLY message to site Si and places the request of site Si on request_queuej

### To execute the critical section:
- A site Si can enter the critical section if it has received the message with timestamp larger than (tsi, i) from all other sites and its own request is at the top of request_queuei

### To release the critical section:
- When a site Si exits the critical section, it removes its own request from the top of its request queue and sends a timestamped RELEASE message to all other sites
- When a site Sj receives the timestamped RELEASE message from site Si, it removes the request of Si from its request queue

### Drawbacks of Lamport’s Algorithm:
- Unreliable approach: failure of any one of the processes will halt the progress of entire system.
- High message complexity: Algorithm requires 3(N-1) messages per critical section invocation.

### Performance:
- Synchronization delay is equal to maximum message transmission time
- It requires 3(N – 1) messages per CS execution.
- Algorithm can be optimized to 2(N – 1) messages by omitting the REPLY message in some situations.



## Ricart and Agrawala

Ricart–Agrawala algorithm is an algorithm to for mutual exclusion in a distributed system proposed by Glenn Ricart and Ashok Agrawala. This algorithm is an extension and optimization of Lamport’s Distributed Mutual Exclusion Algorithm. Like Lamport’s Algorithm, it also follows permission based approach to ensure mutual exclusion.

- Two type of messages ( REQUEST and REPLY) are used and communication channels are assumed to follow FIFO order.
- A site send a REQUEST message to all other site to get their permission to enter critical section.
- A site send a REPLY message to other site to give its permission to enter the critical section.
- A timestamp is given to each critical section request using Lamport’s logical clock.
- Timestamp is used to determine priority of critical section requests. Smaller timestamp gets high priority over larger timestamp. The execution of critical section request is always in the order of their timestamp.

### To enter Critical section:
- When a site Si wants to enter the critical section, it send a timestamped REQUEST message to all other sites.
- When a site Sj receives a REQUEST message from site Si, It sends a REPLY message to site Si if and only if
- Site Sj is neither requesting nor currently executing the critical section.
- In case Site Sj is requesting, the timestamp of Site Si‘s request is smaller than its own request.
Otherwise the request is deferred by site Sj.

### To execute the critical section:
- Site Si enters the critical section if it has received the REPLY message from all other sites.
### To release the critical section:
- Upon exiting site Si sends REPLY message to all the deferred requests.

### Drawbacks of Ricart–Agrawala algorithm:
- Unreliable approach: failure of any one of node in the system can halt the progress of the system. In this situation, the process will starve forever.
The problem of failure of node can be solved by detecting failure after some timeout.

### Performance:
- Synchronization delay is equal to maximum message transmission time
- It requires 2(N – 1) messages per Critical section execution
