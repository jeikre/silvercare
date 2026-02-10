<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="header.jsp" %>
<%@ include file="navbar.jsp" %>

<div class="hero-container">
    <div class="hero-content">
        <h1>Compassionate Care for Your Loved Ones</h1>
        <p>
            We support seniors with in-home care, assisted living help, and dementia care.
            Families can book services and manage care easily through our online platform.
        </p>
        
          <%
    String qHome = request.getParameter("q");
    if (qHome == null) qHome = "";
    %>

    <form method="get"
          action="<%= request.getContextPath() %>/services/list"
          class="hero-search-form">
        <input type="text" name="q" value="<%= qHome %>"
               class="hero-search-input"
               placeholder="Search services (e.g. night care, vitals, transport)">
        <button type="submit" class="hero-search-btn">Search</button>
    </form>
<div class="popular-searches">
    <span class="popular-label">Popular:</span>

    <a class="popular-item" 
       href="<%=request.getContextPath()%>/services/list?q=home">
       Home Care
    </a>

    <a class="popular-item" 
       href="<%=request.getContextPath()%>/services/list?q=transport">
       Transport
    </a>

    <a class="popular-item" 
       href="<%=request.getContextPath()%>/services/list?q=personal care">
       Personal Care
    </a>

    <a class="popular-item" 
       href="<%=request.getContextPath()%>/services/list?q=medical">
       Medical Check
    </a>
</div>
    
        <div class="hero-buttons">
            <a href="<%= request.getContextPath() %>/services/categories" class="btn-primary">Explore Our Services</a>
            <a href="<%= request.getContextPath() %>/index.jsp#about" class="btn-secondary">Learn More</a>
        </div>
    </div>

    <div class="hero-image-box">
        <img src="<%= request.getContextPath() %>/images/homepagebanner.jpg"
             class="hero-image" alt="Silver Care - Elderly Care Services">
    </div>
</div>

<section class="stats-section fade-in-section scale-in">
    <div class="stat-item">
        <div class="stat-number">15+</div>
        <div class="stat-label">Years Experience</div>
    </div>
    <div class="stat-item">
        <div class="stat-number">2,500+</div>
        <div class="stat-label">Seniors Served</div>
    </div>
    <div class="stat-item">
        <div class="stat-number">98%</div>
        <div class="stat-label">Satisfaction Rate</div>
    </div>
    <div class="stat-item">
        <div class="stat-number">24/7</div>
        <div class="stat-label">Support Available</div>
    </div>
</section>

<section class="features-section fade-in-section" id="about">
    <h2 class="features-title">Why Choose Silver Care?</h2>
    <p class="features-subtitle">
        We provide comprehensive care services designed to help seniors live comfortably and independently
    </p>

    <div class="features-grid">
        <div class="feature-card">
            <div class="feature-icon">👨‍⚕️</div>
            <h3>Professional Care</h3>
            <p>Our trained caregivers provide compassionate, professional service tailored to each individual's needs.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">🏠</div>
            <h3>Home Comfort</h3>
            <p>Quality care in the comfort and familiarity of your loved one's own home environment.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">🛡️</div>
            <h3>Trusted Support</h3>
            <p>24/7 availability with background-checked staff dedicated to providing reliable, trustworthy care.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">🚗</div>
            <h3>Safe Transport</h3>
            <p>Comfortable and secure transportation services for medical appointments and daily activities.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">💊</div>
            <h3>Medication Management</h3>
            <p>Careful monitoring and administration of medications to ensure proper dosage and timing.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">❤️</div>
            <h3>Companionship</h3>
            <p>Engaging social interaction and emotional support to combat loneliness and promote wellbeing.</p>
        </div>
    </div>
</section>

<section class="services-showcase fade-in-section">
    <div class="showcase-content">
        <div class="showcase-text">
            <h2>Comprehensive Care Services</h2>
            <p>
                From daily grooming and personal hygiene to wellness monitoring and safe transportation,
                we offer a full range of services designed to support your loved ones' health and happiness.
            </p>

            <ul class="service-list">
                <li>✓ Personal Grooming &amp; Hygiene Care</li>
                <li>✓ 24/7 In-Home Care &amp; Companionship</li>
                <li>✓ Health Monitoring &amp; Wellness Support</li>
                <li>✓ Safe &amp; Reliable Transportation</li>
            </ul>

            <a href="<%= request.getContextPath() %>/services/categories" class="btn-primary">View All Services</a>
        </div>

        <div class="showcase-image">
            <img src="<%= request.getContextPath() %>/images/services_showcase.webp" alt="Silver Care Services">
        </div>
    </div>
</section>

<section class="testimonials-section fade-in-section">
    <h2 class="section-title">What Families Say About Us</h2>

    <div class="testimonials-grid">
        <div class="testimonial-card">
            <div class="quote-mark">"</div>
            <p class="testimonial-text">
                Silver Care has been a blessing for our family. The caregivers are professional, kind,
                and truly care about my mother's wellbeing.
            </p>
            <div class="testimonial-author">
                <strong>Sarah Chen</strong>
                <span>Daughter of Client</span>
            </div>
        </div>

        <div class="testimonial-card">
            <div class="quote-mark">"</div>
            <p class="testimonial-text">
                The transport service is excellent. Always on time, safe, and the drivers are so patient with my father.
            </p>
            <div class="testimonial-author">
                <strong>Michael Tan</strong>
                <span>Son of Client</span>
            </div>
        </div>

        <div class="testimonial-card">
            <div class="quote-mark">"</div>
            <p class="testimonial-text">
                We couldn't be happier with the home sitting services. It gives us peace of mind knowing Mom is in good hands.
            </p>
            <div class="testimonial-author">
                <strong>Linda Wong</strong>
                <span>Family Member</span>
            </div>
        </div>
    </div>
</section>

<section class="cta-section fade-in-section scale-in">
    <div class="cta-content">
        <h2>Ready to Provide the Best Care?</h2>
        <p>Contact us today to discuss how we can support your loved ones with our comprehensive care services.</p>

        <div class="cta-buttons">
            <a href="<%= request.getContextPath() %>/products/products.jsp" class="btn-primary">
                Get Started With Our Products
            </a>
        </div>
    </div>
</section>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Smooth scroll for anchor links (in-page)
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

    // Intersection Observer for fade-in animations
    const observerOptions = { threshold: 0.1, rootMargin: '0px 0px -50px 0px' };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) entry.target.classList.add('visible');
        });
    }, observerOptions);

    document.querySelectorAll('.fade-in-section').forEach(section => observer.observe(section));

    // Hero parallax effect
    window.addEventListener('scroll', function() {
        const scrolled = window.pageYOffset;
        const hero = document.querySelector('.hero-container');
        if (hero && scrolled < window.innerHeight) {
            hero.style.transform = `translateY(${scrolled * 0.5}px)`;
            hero.style.opacity = 1 - (scrolled / window.innerHeight) * 0.5;
        }
    });
});
</script>

<%@ include file="footer.jsp" %>
