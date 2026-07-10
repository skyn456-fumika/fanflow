import { Outlet } from 'react-router-dom'
import Header from './Header'

function Layout() {
  return (
    <div>
      <Header />

      <main className="page-container">
        <Outlet />
      </main>
    </div>
  )
}

export default Layout