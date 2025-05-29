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

CREATE TABLE chapters
(
    chapter_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       TEXT NOT NULL,
    course_id  UUID NOT NULL REFERENCES courses (course_id) ON DELETE CASCADE
);

CREATE TABLE questions
(
    question_id     UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    text            TEXT NOT NULL,
    sample_solution TEXT NOT NULL,
    chapter_id      UUID NOT NULL REFERENCES chapters (chapter_id) ON DELETE CASCADE,
    ordering        INT  NOT NULL DEFAULT 1
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
