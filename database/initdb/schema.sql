CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE users
(
    user_id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          TEXT        NOT NULL,
    email         TEXT UNIQUE NOT NULL,
    password_hash TEXT        NOT NULL
);

CREATE TABLE courses
(
    course_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title     TEXT NOT NULL
);

CREATE TABLE user_courses
(
    user_id   UUID REFERENCES users (user_id) ON DELETE CASCADE,
    course_id UUID REFERENCES courses (course_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, course_id)
);

CREATE TABLE quizzes
(
    quiz_id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title     TEXT NOT NULL,
    max_score REAL NOT NULL    DEFAULT 100.0
);


CREATE TABLE questions
(
    question_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    text        TEXT NOT NULL,
    type        TEXT NOT NULL CHECK (type IN ('MCQ', 'OPEN'))
);

CREATE TABLE quiz_questions
(
    quiz_id     UUID NOT NULL REFERENCES quizzes (quiz_id) ON DELETE CASCADE,
    question_id UUID NOT NULL
        REFERENCES questions (question_id) ON DELETE CASCADE,
    ordering    INT  NOT NULL,
    PRIMARY KEY (quiz_id, question_id)
);

CREATE TABLE options
(
    option_id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    question_id UUID    NOT NULL REFERENCES questions (question_id) ON DELETE CASCADE,
    text        TEXT    NOT NULL,
    is_correct  BOOLEAN NOT NULL
);

CREATE TABLE open_question
(
    question_id  UUID PRIMARY KEY REFERENCES questions (question_id) ON DELETE CASCADE,
    model_answer TEXT NOT NULL
);

CREATE TABLE feedback
(
    feedback_id    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    generated_text TEXT NOT NULL,
    missing_points TEXT[],
    suggestions    TEXT[]
);

CREATE TABLE flashcard_decks
(
    deck_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    name    TEXT NOT NULL
);

CREATE TABLE flashcards
(
    flashcard_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    deck_id      UUID NOT NULL REFERENCES flashcard_decks (deck_id) ON DELETE CASCADE,
    front        TEXT NOT NULL,
    back         TEXT NOT NULL
);
