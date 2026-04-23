## Report – Question Answers

---

### Part 1 – Service Architecture & Setup

**Q: Explain the default lifecycle of a JAX-RS Resource class. How does this affect in-memory data management?**

In JAX-RS, a new resource object is created for every request. So each request is handled separately.

Because of this, we cannot store shared data inside the resource class itself, since it will be lost after the request finishes. To solve this, I used a singleton DataStore class. This keeps the data in memory and allows all requests to access the same data safely.

---

**Q: Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS means the API provides links to related resources in the response.

This helps clients understand what they can do next without hardcoding URLs. It makes the API easier to use and more flexible if the structure changes later.

---

### Part 2 – Room Management

**Q: What are the implications of returning only IDs versus full room objects?**

Returning only IDs uses less data, which is more efficient.

However, the client would need to make more requests to get full details. Returning full objects gives more information in one response and is easier for the client to use.

---

**Q: Is the DELETE operation idempotent in your implementation?**

Yes, DELETE is idempotent.

If a room is deleted once, sending the same request again will not change anything. The room will already be removed, so the system state stays the same.

---

### Part 3 – Sensor Operations

**Q: What happens if a client sends data in a format other than application/json?**

The API is set to accept only JSON using @Consumes.

If a different format is sent, the request will be rejected with a 415 Unsupported Media Type error.

---

**Q: Why is @QueryParam considered superior to path-based filtering?**

Query parameters are better for filtering because they are optional and flexible.

For example, /sensors?type=CO2 allows filtering without changing the main resource. Path-based filtering would make the URL more complicated and harder to manage.

---

### Part 4 – Sub-Resources

**Q: What are the architectural benefits of the Sub-Resource Locator pattern?**

The sub-resource locator pattern helps separate logic into smaller classes.

Instead of handling everything in one class, different resources handle their own responsibilities. This makes the code easier to read, maintain, and extend.

---

### Part 5 – Error Handling & Logging

**Q: Why is HTTP 422 more semantically accurate than 404 for a missing roomId reference?**

A 404 error means the endpoint does not exist.

In this case, the endpoint is correct, but the data inside the request is wrong. So 422 is more accurate because it shows that the request is valid but the data is incorrect.

---

**Q: What are the security risks of exposing internal Java stack traces?**

Stack traces can show internal details like class names and system structure.

This can help attackers understand how the system works and find weaknesses. So the API hides this information and returns a general error message instead.

---

**Q: Why is it better to use JAX-RS filters for logging instead of adding Logger calls manually to every method?**

Filters allow logging to be handled in one place.

This avoids repeating code in every method and ensures all requests and responses are logged consistently.