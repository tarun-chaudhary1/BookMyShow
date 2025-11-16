// app.js - Single client side script for all pages
const app = (function () {
  // base API path - change if your app uses a prefix
  const API_BASE = '/api';

  // utilities
  function qs(sel) { return document.querySelector(sel); }
  function qsa(sel) { return Array.from(document.querySelectorAll(sel)); }
  function formatDateTime(dtStr) {
    const d = new Date(dtStr);
    return d.toLocaleString();
  }

  // ---- INDEX PAGE ----
  async function loadMovies() {
    try {
      const res = await fetch(`${API_BASE}/movies`);
      const movies = await res.json();
      const container = qs('#movies');
      container.innerHTML = '';
      const tpl = qs('#movie-card').content;
      movies.forEach(m => {
        const node = tpl.cloneNode(true);
        node.querySelector('.poster').src = m.posterUrl || 'https://via.placeholder.com/400x300?text=Poster';
        node.querySelector('.title').textContent = m.title || m.name || 'Untitled';
        node.querySelector('.meta').textContent = `${m.language || ''} • ${m.genre || ''} • ${m.durationMins || ''} min`;
        node.querySelector('.desc').textContent = m.description || '';
        node.querySelector('.view').href = `movie.html?id=${m.id}`;
        node.querySelector('.book').addEventListener('click', () => {
          // take user to movie page (first show selection)
          window.location = `movie.html?id=${m.id}`;
        });
        container.appendChild(node);
      });
    } catch (err) {
      console.error(err);
      qs('#movies').textContent = 'Failed to load movies';
    }
  }

  // ---- MOVIE PAGE ----
  async function loadMoviePage() {
    const params = new URLSearchParams(location.search);
    const movieId = params.get('id');
    if (!movieId) {
      qs('#movie-detail').innerHTML = '<p>No movie selected</p>';
      return;
    }
    // fetch movie
    const [movieRes, showsRes] = await Promise.all([
      fetch(`${API_BASE}/movies/${movieId}`),
      // ASSUMPTION: endpoint for shows by movie id
      fetch(`${API_BASE}/shows?movieId=${movieId}`)
    ]);
    if (!movieRes.ok) {
      qs('#movie-detail').innerHTML = '<p>Movie not found</p>';
      return;
    }
    const movie = await movieRes.json();
    const shows = showsRes.ok ? await showsRes.json() : [];

    // render movie
    const md = qs('#movie-detail');
    md.innerHTML = `
      <img src="${movie.posterUrl || 'https://via.placeholder.com/400x600?text=Poster'}" alt="poster"/>
      <div>
        <h1>${movie.title || movie.name}</h1>
        <p class="meta">${movie.language || ''} • ${movie.genre || ''} • ${movie.durationMins || ''} min</p>
        <p>${movie.description || ''}</p>
      </div>
    `;

    // render shows
    const showsDiv = qs('#shows');
    showsDiv.innerHTML = '';
    const tpl = qs('#show-card').content;
    shows.forEach(s => {
      const node = tpl.cloneNode(true);
      node.querySelector('.time').textContent = new Date(s.startTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
      node.querySelector('.range').textContent = `${formatDateTime(s.startTime)} — ${formatDateTime(s.endTime)}`;
      node.querySelector('.book').addEventListener('click', () => {
        // store selected show and go to seats page
        localStorage.setItem('selectedShowId', s.id);
        localStorage.setItem('selectedMovie', JSON.stringify(movie));
        location = `seats.html?showId=${s.id}`;
      });
      showsDiv.appendChild(node);
    });
  }

  // ---- SEAT SELECTION PAGE ----
  async function loadSeatPage() {
    // showId priority from query param
    const params = new URLSearchParams(location.search);
    const showId = params.get('showId') || localStorage.getItem('selectedShowId');
    if (!showId) {
      qs('#show-info').innerHTML = '<p>No show selected.</p>';
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/shows/${showId}`);
      if (!res.ok) {
        qs('#show-info').innerHTML = '<p>Show not found.</p>';
        return;
      }
      const show = await res.json();
      renderShowInfo(show);
      renderSeatMap(show);
      setupConfirm(show);
    } catch (err) {
      console.error(err);
      qs('#message').textContent = 'Failed to load show';
    }
  }

  function renderShowInfo(show) {
    const el = qs('#show-info');
    el.innerHTML = `
      <div><strong>${show.movie?.title || show.movie?.name}</strong></div>
      <div>${formatDateTime(show.startTime)} — ${formatDateTime(show.endTime)}</div>
      <div>Screen: ${show.screen?.name || ''}</div>
    `;
  }

  function renderSeatMap(show) {
    const map = qs('#seat-map');
    map.innerHTML = '';
    // availableSeats is an array of ShowSeatDto { id, seat:{id,seatNumber,seatType,basePrice}, status, price }
    const seats = show.availableSeats || [];
    seats.forEach(ss => {
      const d = document.createElement('div');
      d.className = 'seat';
      if (ss.status && ss.status.toUpperCase() !== 'AVAILABLE' && ss.status.toUpperCase() !== 'FREE') {
        d.classList.add('booked');
      }
      d.dataset.showSeatId = ss.id;
      d.dataset.seatId = ss.seat?.id;
      d.dataset.price = ss.price || ss.seat?.basePrice || 0;
      d.textContent = ss.seat?.seatNumber || 'S';
      d.addEventListener('click', () => toggleSeatSelect(d));
      map.appendChild(d);
    });
    updateSelectionUI();
  }

  function toggleSeatSelect(node) {
    if (node.classList.contains('booked')) return;
    node.classList.toggle('selected');
    updateSelectionUI();
  }

  function updateSelectionUI() {
    const selected = qsa('.seat.selected');
    const countEl = qs('#selected-count');
    const amountEl = qs('#selected-amount');
    let total = 0;
    selected.forEach(s => total += parseFloat(s.dataset.price || 0));
    countEl.textContent = selected.length;
    amountEl.textContent = total.toFixed(2);
  }

  function setupConfirm(show) {
    const btn = qs('#confirm');
    const msg = qs('#message');
    btn.onclick = async () => {
      msg.textContent = '';
      const selected = qsa('.seat.selected');
      if (selected.length === 0) {
        msg.textContent = 'Please select at least one seat.';
        return;
      }
      // build booking request using BookingRequestDto
      const seatIds = selected.map(s => parseInt(s.dataset.seatId));
      const paymentMethod = qs('#payment-method').value || 'UPI';
      // In a real app you'd pick the logged user. Here we let userId=1 for demo or you could prompt.
      const bookingReq = {
        userId: 1,
        showId: show.id,
        seatIds: seatIds,
        paymentMethod: paymentMethod
      };

      try {
        const res = await fetch(`${API_BASE}/bookings`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(bookingReq)
        });
        if (res.status === 201 || res.ok) {
          const booking = await res.json();
          // store booking id and redirect to booking page to view details
          localStorage.setItem('lastBookingId', booking.id);
          location = `booking.html?id=${booking.id}`;
        } else {
          const err = await res.json().catch(()=>({message:'Booking failed'}));
          msg.textContent = err.message || 'Booking failed';
        }
      } catch (e) {
        console.error(e);
        msg.textContent = 'Network error while booking';
      }
    };
  }

  // ---- BOOKING PAGE ----
  async function loadBookingPage() {
    // If page query param id exists, use it; otherwise try lastBookingId
    const params = new URLSearchParams(location.search);
    const id = params.get('id') || localStorage.getItem('lastBookingId');
    const container = qs('#booking-container');
    container.innerHTML = '';
    if (!id) {
      container.innerHTML = '<p>No booking to show</p>';
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/bookings/${id}`);
      if (!res.ok) {
        container.innerHTML = '<p>Booking not found</p>';
        return;
      }
      const b = await res.json();
      container.innerHTML = renderBooking(b);
    } catch (err) {
      console.error(err);
      container.innerHTML = '<p>Failed to load booking</p>';
    }
  }

  function renderBooking(b) {
    const seatsHtml = (b.seats || []).map(s => `<li>${s.seat?.seatNumber} (${s.seat?.seatType}) - ₹${s.price}</li>`).join('');
    return `
      <div class="card" style="padding:12px">
        <h3>Booking #${b.bookingNumber}</h3>
        <p><strong>User:</strong> ${b.user?.name || 'N/A'}</p>
        <p><strong>Show:</strong> ${b.show?.movie?.title || b.show?.movie?.name} - ${formatDateTime(b.bookingTime)}</p>
        <p><strong>Status:</strong> ${b.status}</p>
        <p><strong>Total:</strong> ₹${b.totalPrice}</p>
        <h4>Seats</h4>
        <ul>${seatsHtml}</ul>
        <h4>Payment</h4>
        <p>${b.payment ? `${b.payment.paymentMethod} - ${b.payment.status} - ${b.payment.transactionId}` : 'N/A'}</p>
      </div>
    `;
  }

  return {
    loadMovies,
    loadMoviePage,
    loadSeatPage,
    loadBookingPage
  };
})();
