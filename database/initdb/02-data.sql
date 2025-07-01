-- Insert initial course data
INSERT INTO courses (course_id, title) VALUES 
    ('550e8400-e29b-41d4-a716-446655440000', 'DevOps Fundamentals'),
    ('550e8400-e29b-41d4-a716-446655441000', 'Placeholder Course 1'),
    ('550e8400-e29b-41d4-a716-446655442000', 'Placeholder Course 2');

-- Insert DevOps chapters
INSERT INTO chapters (chapter_id, name, course_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'CI/CD Pipelines', '550e8400-e29b-41d4-a716-446655440000'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Infrastructure as Code', '550e8400-e29b-41d4-a716-446655440000'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Monitoring and Observability', '550e8400-e29b-41d4-a716-446655440000');

-- Insert Placeholder Course 1 chapters
INSERT INTO chapters (chapter_id, name, course_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655441001', 'Placeholder Chapter 1A', '550e8400-e29b-41d4-a716-446655441000'),
    ('550e8400-e29b-41d4-a716-446655441002', 'Placeholder Chapter 1B', '550e8400-e29b-41d4-a716-446655441000');

-- Insert Placeholder Course 2 chapters
INSERT INTO chapters (chapter_id, name, course_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655442001', 'Placeholder Chapter 2A', '550e8400-e29b-41d4-a716-446655442000'),
    ('550e8400-e29b-41d4-a716-446655442002', 'Placeholder Chapter 2B', '550e8400-e29b-41d4-a716-446655442000');

-- Insert sample questions for Placeholder Course 1
INSERT INTO questions (question_id, text, sample_solution, chapter_id, ordering) VALUES 
    ('550e8400-e29b-41d4-a716-446655441011', 
     'What are the fundamental principles of software testing?',
     'The fundamental principles of software testing include: 1) Testing shows the presence of defects, not their absence, 2) Exhaustive testing is impossible, 3) Early testing saves time and money, 4) Defects cluster together, 5) Testing is context-dependent, 6) The pesticide paradox - tests need to be updated regularly, and 7) Absence-of-errors fallacy - finding defects does not guarantee system usability.',
     '550e8400-e29b-41d4-a716-446655441001', 1),
    ('550e8400-e29b-41d4-a716-446655441012',
     'Explain the difference between verification and validation in testing.',
     'Verification is the process of checking whether the product is being built correctly according to specifications (Are we building the product right?). Validation is the process of checking whether the correct product is being built according to user requirements (Are we building the right product?). Verification typically involves static testing methods like reviews and inspections, while validation involves dynamic testing by executing the code.',
     '550e8400-e29b-41d4-a716-446655441002', 1);

-- Insert sample questions for Placeholder Course 2  
INSERT INTO questions (question_id, text, sample_solution, chapter_id, ordering) VALUES 
    ('550e8400-e29b-41d4-a716-446655442011', 
     'Describe advanced testing strategies for complex systems.',
     'Advanced testing strategies for complex systems include: 1) Risk-based testing to prioritize test efforts, 2) Model-based testing using formal models, 3) Property-based testing with automated test case generation, 4) Mutation testing to evaluate test suite quality, 5) Exploratory testing for discovering unexpected behaviors, 6) Performance testing under realistic loads, and 7) Security testing to identify vulnerabilities.',
     '550e8400-e29b-41d4-a716-446655442001', 1),
    ('550e8400-e29b-41d4-a716-446655442012',
     'How do you implement continuous testing in modern development workflows?',
     'Continuous testing in modern development workflows involves: 1) Integrating automated tests into CI/CD pipelines, 2) Implementing multiple test layers (unit, integration, system, acceptance), 3) Using test automation frameworks and tools, 4) Parallel test execution for faster feedback, 5) Test data management and environment provisioning, 6) Real-time monitoring and reporting, 7) Shift-left testing practices, and 8) Continuous feedback loops for test improvement.',
     '550e8400-e29b-41d4-a716-446655442002', 1);

-- Insert questions for Chapter 1: CI/CD Pipelines
INSERT INTO questions (question_id, text, sample_solution, chapter_id, ordering) VALUES 
    ('550e8400-e29b-41d4-a716-446655440012', 
     'Explain the benefits of implementing a CI/CD pipeline in your development process.',
     'CI/CD pipelines provide several key benefits: 1) Faster time to market through automated testing and deployment, 2) Reduced human error through automation, 3) Consistent and repeatable deployments, 4) Early detection of integration issues, 5) Improved code quality through automated testing, 6) Better collaboration between development and operations teams, and 7) Increased deployment confidence and reduced rollback time.',
     '550e8400-e29b-41d4-a716-446655440001', 1),
    ('550e8400-e29b-41d4-a716-446655440013',
     'Describe the key stages of a typical CI/CD pipeline and their purposes.',
     'A typical CI/CD pipeline includes: 1) Source Control - developers commit code changes, 2) Build Stage - code is compiled and packaged, 3) Test Stage - automated unit, integration, and acceptance tests are run, 4) Code Analysis - static analysis and security scans, 5) Deployment to Staging - testing in production-like environment, 6) Production Deployment - automated or manual release to production, 7) Monitoring - continuous observation of application performance and health.',
     '550e8400-e29b-41d4-a716-446655440001', 2),
    ('550e8400-e29b-41d4-a716-446655440014',
     'What are the main differences between Continuous Integration and Continuous Deployment?',
     'Continuous Integration (CI) focuses on frequently merging code changes and running automated tests to detect integration issues early. Continuous Deployment (CD) extends CI by automatically deploying every code change that passes tests to production. Continuous Delivery is a middle ground where code is automatically prepared for production deployment but requires manual approval for release.',
     '550e8400-e29b-41d4-a716-446655440001', 3);

-- Insert questions for Chapter 2: Infrastructure as Code
INSERT INTO questions (question_id, text, sample_solution, chapter_id, ordering) VALUES 
    ('550e8400-e29b-41d4-a716-446655440022',
     'Describe how Infrastructure as Code improves deployment consistency and reliability.',
     'Infrastructure as Code improves deployment consistency and reliability by: 1) Defining infrastructure in version-controlled code, ensuring reproducible environments, 2) Eliminating configuration drift through declarative specifications, 3) Enabling automated provisioning and scaling, 4) Providing infrastructure testing and validation capabilities, 5) Facilitating disaster recovery through quick environment recreation, 6) Improving documentation through code-based infrastructure definitions, and 7) Enabling consistent environments across development, staging, and production.',
     '550e8400-e29b-41d4-a716-446655440002', 1),
    ('550e8400-e29b-41d4-a716-446655440023',
     'Compare and contrast declarative vs imperative approaches in Infrastructure as Code.',
     'Declarative IaC describes the desired end state of infrastructure (what you want), while imperative IaC specifies the exact steps to achieve that state (how to get there). Declarative tools like Terraform and CloudFormation focus on the desired configuration, automatically determining the necessary changes. Imperative tools like Ansible and shell scripts require explicit step-by-step instructions. Declarative approaches are generally more predictable and easier to maintain.',
     '550e8400-e29b-41d4-a716-446655440002', 2),
    ('550e8400-e29b-41d4-a716-446655440024',
     'Explain the concept of infrastructure drift and how IaC tools help prevent it.',
     'Infrastructure drift occurs when the actual infrastructure configuration deviates from the defined specification over time due to manual changes, patches, or updates. IaC tools prevent drift by: 1) Maintaining a single source of truth in code, 2) Regularly comparing actual state with desired state, 3) Detecting and reporting configuration differences, 4) Enabling automated remediation to restore desired state, 5) Providing audit trails of all changes, and 6) Preventing unauthorized manual modifications.',
     '550e8400-e29b-41d4-a716-446655440002', 3);

-- Insert questions for Chapter 3: Monitoring and Observability
INSERT INTO questions (question_id, text, sample_solution, chapter_id, ordering) VALUES 
    ('550e8400-e29b-41d4-a716-446655440032',
     'Explain the difference between monitoring and observability in DevOps practices.',
     'Monitoring typically focuses on predefined metrics and alerts based on known failure modes, providing visibility into system health through dashboards and notifications. Observability goes beyond monitoring by providing the ability to understand system behavior from the outside without prior knowledge of internal workings. While monitoring answers "what is happening," observability helps answer "why it is happening" through rich data collection (metrics, logs, traces) and correlation capabilities, enabling teams to debug unknown issues and understand complex system interactions.',
     '550e8400-e29b-41d4-a716-446655440003', 1),
    ('550e8400-e29b-41d4-a716-446655440033',
     'Describe the three pillars of observability and provide examples of each.',
     'The three pillars of observability are: 1) Metrics - numerical measurements of system performance over time (CPU usage, response times, error rates), 2) Logs - time-ordered records of events and transactions (application logs, system logs, audit logs), 3) Traces - records of requests as they flow through distributed systems, showing the path and timing of operations across multiple services. Together, they provide comprehensive visibility into system behavior.',
     '550e8400-e29b-41d4-a716-446655440003', 2),
    ('550e8400-e29b-41d4-a716-446655440034',
     'How do distributed tracing and logging work together to provide system insights?',
     'Distributed tracing tracks requests across multiple services, providing end-to-end visibility of transaction flows and identifying performance bottlenecks. Logging captures detailed events and errors at each service level. Together, they enable: 1) Correlation of trace spans with relevant log entries, 2) Root cause analysis by following trace paths and examining logs, 3) Performance optimization by identifying slow operations in traces and their corresponding log details, 4) Error investigation by linking failed traces to error logs, and 5) System understanding through combined transaction flows and detailed event records.',
     '550e8400-e29b-41d4-a716-446655440003', 3);
