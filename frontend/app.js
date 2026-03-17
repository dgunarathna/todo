const API_BASE = '/api/tasks';

const taskListEl = document.getElementById('task-list');
const emptyStateEl = document.getElementById('empty-state');
const formEl = document.getElementById('task-form');
const titleInput = document.getElementById('title');
const descriptionInput = document.getElementById('description');
const formErrorEl = document.getElementById('form-error');

async function fetchTasks() {
  try {
    const res = await fetch(API_BASE);
    if (!res.ok) {
      throw new Error('Failed to load tasks');
    }
    const tasks = await res.json();
    renderTasks(tasks);
  } catch (err) {
    console.error(err);
    showFormError('Could not load tasks. Please try again.');
  }
}

function renderTasks(tasks) {
  taskListEl.innerHTML = '';

  if (!tasks || tasks.length === 0) {
    emptyStateEl.hidden = false;
    return;
  }

  emptyStateEl.hidden = true;

  tasks.forEach(task => {
    const card = document.createElement('article');
    card.className = 'task-card';
    card.dataset.id = task.id;

    const main = document.createElement('div');
    main.className = 'task-card__main';

    const titleEl = document.createElement('h3');
    titleEl.className = 'task-card__title';
    titleEl.textContent = task.title;
    main.appendChild(titleEl);

    if (task.description) {
      const descEl = document.createElement('p');
      descEl.className = 'task-card__description';
      descEl.textContent = task.description;
      main.appendChild(descEl);
    }

    if (task.createdAt) {
      const metaEl = document.createElement('p');
      metaEl.className = 'task-card__meta';
      const created = new Date(task.createdAt);
      metaEl.textContent = `Created ${created.toLocaleString()}`;
      main.appendChild(metaEl);
    }

    const actions = document.createElement('div');
    actions.className = 'task-card__actions';

    const doneBtn = document.createElement('button');
    doneBtn.className = 'btn btn--done';
    doneBtn.type = 'button';
    doneBtn.textContent = 'Done';
    doneBtn.addEventListener('click', () => handleDone(task.id, card));

    actions.appendChild(doneBtn);

    card.appendChild(main);
    card.appendChild(actions);
    taskListEl.appendChild(card);
  });
}

async function handleDone(id, cardEl) {
  try {
    const res = await fetch(`${API_BASE}/${id}/done`, { method: 'POST' });
    if (!res.ok) {
      throw new Error('Failed to mark task as done');
    }
    // Optimistically remove from list
    cardEl.remove();
    if (taskListEl.children.length === 0) {
      emptyStateEl.hidden = false;
    }
  } catch (err) {
    console.error(err);
    showFormError('Could not mark task as done. Please try again.');
  }
}

function showFormError(message) {
  formErrorEl.textContent = message;
  formErrorEl.hidden = false;
}

function clearFormError() {
  formErrorEl.textContent = '';
  formErrorEl.hidden = true;
}

formEl.addEventListener('submit', async (event) => {
  event.preventDefault();
  clearFormError();

  const title = titleInput.value.trim();
  const description = descriptionInput.value.trim();

  if (!title) {
    showFormError('Title is required.');
    return;
  }

  try {
    const res = await fetch(API_BASE, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ title, description })
    });

    if (!res.ok) {
      if (res.status === 400) {
        showFormError('Please check the task details and try again.');
      } else {
        showFormError('Could not create task. Please try again.');
      }
      return;
    }

    titleInput.value = '';
    descriptionInput.value = '';

    await fetchTasks();
  } catch (err) {
    console.error(err);
    showFormError('Could not create task. Please try again.');
  }
});

window.addEventListener('DOMContentLoaded', () => {
  fetchTasks();
});

